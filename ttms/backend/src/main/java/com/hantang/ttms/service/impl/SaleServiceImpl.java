package com.hantang.ttms.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Customer;
import com.hantang.ttms.domain.Employee;
import com.hantang.ttms.domain.Sale;
import com.hantang.ttms.domain.SaleItem;
import com.hantang.ttms.domain.SaleStatus;
import com.hantang.ttms.domain.SaleType;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.OrderRequest;
import com.hantang.ttms.dto.SaleResponse;
import com.hantang.ttms.dto.TicketMapper;
import com.hantang.ttms.repository.CustomerRepository;
import com.hantang.ttms.repository.EmployeeRepository;
import com.hantang.ttms.repository.SaleRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.SaleService;

/**
 * 售票/退票业务服务实现（核心交易模块）
 *
 * 负责完整的售票生命周期管理：
 *
 * 下单流程（placeOrder）：
 * - 校验票 ID 是否存在
 * - 释放超时锁座（过期 LOCKED → AVAILABLE）
 * - 判断销售渠道（有 employeeId=COUNTER，否则=ONLINE）
 * - 锁定所有票（AVAILABLE → LOCKED，设置 lockTime）
 * - 创建订单及明细
 *
 * 收款流程（makePayment）：
 * - 校验订单状态为 PENDING_PAYMENT
 * - 校验支付金额 >= 订单总金额
 * - 校验锁座未超时（防止过期锁座被收款）
 * - 更新票状态 LOCKED → SOLD
 * - 更新订单状态 PENDING_PAYMENT → PAID
 *
 * 退票流程（refund）：
 * - 校验订单状态为 PAID
 * - 已验票不可退票
 * - 更新票状态 SOLD/CHECKED → REFUNDED
 * - 线上支付：退款到客户钱包余额
 * - 更新订单状态 PAID → REFUNDED
 *
 * 锁座超时配置：ttms.ticket.lock-timeout-minutes（默认 10 分钟）
 */
@Service
public class SaleServiceImpl implements SaleService {
    private final TicketRepository ticketRepository;
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    /** 锁座超时时间（分钟），从配置文件注入，默认 10 分钟 */
    private final long lockTimeoutMinutes;

    /**
     * 构造函数注入全部依赖
     * @param lockTimeoutMinutes 锁座超时分钟数，由 ttms.ticket.lock-timeout-minutes 配置
     */
    public SaleServiceImpl(
        TicketRepository ticketRepository,
        SaleRepository saleRepository,
        CustomerRepository customerRepository,
        EmployeeRepository employeeRepository,
        @Value("${ttms.ticket.lock-timeout-minutes:10}") long lockTimeoutMinutes
    ) {
        this.ticketRepository = ticketRepository;
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.lockTimeoutMinutes = lockTimeoutMinutes;
    }

    /**
     * {@inheritDoc}
     *
     * 实现要点：
     * 1. 先释放已超时的锁座（让超时锁座回归可售状态）
     * 2. 根据是否有 employeeId 自动判断销售渠道
     * 3. 使用乐观锁（@Version）防止并发售票
     */
    @Override
    @Transactional
    public SaleResponse placeOrder(OrderRequest request) {
        // 校验票是否存在
        List<Ticket> tickets = ticketRepository.findByIdIn(request.ticketIds());
        if (tickets.size() != request.ticketIds().size()) {
            throw new BusinessException("部分票据不存在");
        }

        // 第一步：释放超时锁座，防止超时锁占用可售票
        releaseExpiredLocks(tickets);

        // 第二步：创建订单并设置销售渠道
        Sale sale = new Sale();
        sale.setSaleType(request.employeeId() == null ? SaleType.ONLINE : SaleType.COUNTER);
        if (request.customerId() != null) {
            Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new BusinessException("顾客不存在"));
            sale.setCustomer(customer);
        }
        if (request.employeeId() != null) {
            Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));
            sale.setEmployee(employee);
        }

        // 第三步：逐张票锁定并创建明细
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() != TicketStatus.AVAILABLE) {
                throw new BusinessException("票据不可售或已被锁定");
            }
            ticket.setStatus(TicketStatus.LOCKED);
            ticket.setLockTime(LocalDateTime.now());
            ticketRepository.save(ticket);

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setTicket(ticket);
            item.setPrice(ticket.getPrice());
            sale.getItems().add(item);
        }
        return TicketMapper.toSaleResponse(saleRepository.save(sale));
    }

    /**
     * {@inheritDoc}
     *
     * 实现要点：
     * 1. 先校验订单状态和支付金额
     * 2. 校验每张票的锁座未超时（超时的锁座会抛出异常，需重新下单）
     * 3. 逐张票改 SOLD 状态，锁座时间清空
     */
    @Override
    @Transactional
    public SaleResponse makePayment(Long saleId, BigDecimal paidAmount) {
        Sale sale = saleRepository.findWithItemsById(saleId)
            .orElseThrow(() -> new BusinessException("订单不存在"));
        // 校验订单状态
        if (sale.getStatus() != SaleStatus.PENDING_PAYMENT) {
            throw new BusinessException("订单状态不允许支付");
        }

        // 计算订单总金额
        BigDecimal total = sale.getItems().stream()
            .map(SaleItem::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 校验支付金额
        if (paidAmount.compareTo(total) < 0) {
            throw new BusinessException("支付金额不足");
        }

        // 校验锁座未超时
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(lockTimeoutMinutes);
        for (SaleItem item : sale.getItems()) {
            Ticket ticket = item.getTicket();
            if (ticket.getStatus() != TicketStatus.LOCKED
                || ticket.getLockTime() == null
                || ticket.getLockTime().isBefore(expiredBefore)) {
                throw new BusinessException("锁票已失效，请重新下单");
            }
            ticket.setStatus(TicketStatus.SOLD);
            ticket.setLockTime(null);
            ticketRepository.save(ticket);
        }
        sale.setPaidAmount(paidAmount);
        sale.setChangeAmount(paidAmount.subtract(total));
        sale.setStatus(SaleStatus.PAID);
        return TicketMapper.toSaleResponse(saleRepository.save(sale));
    }

    /**
     * {@inheritDoc}
     *
     * 实现要点：
     * 1. 已验票不可退票（防止恶意退票）
     * 2. 线上支付的订单退款到客户钱包余额
     * 3. 柜台售票退票也记录到订单中
     */
    @Override
    @Transactional
    public SaleResponse refund(Long saleId) {
        Sale sale = saleRepository.findWithItemsById(saleId)
            .orElseThrow(() -> new BusinessException("订单不存在"));
        if (sale.getStatus() != SaleStatus.PAID) {
            throw new BusinessException("订单状态不允许退票");
        }
        // 逐张票退票
        for (SaleItem item : sale.getItems()) {
            Ticket ticket = item.getTicket();
            if (ticket.getStatus() == TicketStatus.CHECKED) {
                throw new BusinessException("已验票不能退票");
            }
            ticket.setStatus(TicketStatus.REFUNDED);
            ticket.setLockTime(null);
            ticketRepository.save(ticket);
        }
        // 恢复客户余额（仅线上支付场景）
        Long refundCustomerId = sale.getCustomerId();
        if (refundCustomerId != null) {
            Customer refundCustomer = customerRepository.findById(refundCustomerId).orElse(null);
            if (refundCustomer != null) {
                refundCustomer.setBalance(refundCustomer.getBalance().add(sale.getPaidAmount()));
                customerRepository.save(refundCustomer);
            }
        }
        sale.setStatus(SaleStatus.REFUNDED);
        saleRepository.save(sale);
        return TicketMapper.toSaleResponse(sale);
    }

    /** {@inheritDoc} */
    @Override
    public SaleResponse get(Long saleId) {
        Sale sale = saleRepository.findWithItemsById(saleId)
            .orElseThrow(() -> new BusinessException("订单不存在"));
        return TicketMapper.toSaleResponse(sale);
    }

    /**
     * {@inheritDoc}
     *
     * 查询逻辑：
     * - 按客户 ID 筛选时直接返回该客户全部订单
     * - 否则按日期 + 可选员工 ID 筛选
     * - 日期为空时默认查询当天
     */
    @Override
    public List<SaleResponse> list(LocalDate date, Long employeeId, Long customerId) {
        if (customerId != null) {
            return saleRepository.findByCustomerId(customerId).stream()
                .map(TicketMapper::toSaleResponse).toList();
        }
        LocalDate target = date == null ? LocalDate.now() : date;
        LocalDateTime start = target.atStartOfDay();
        LocalDateTime end = target.plusDays(1).atStartOfDay();
        List<Sale> sales = employeeId == null
            ? saleRepository.findBySaleTimeBetween(start, end)
            : saleRepository.findByEmployeeIdAndSaleTimeBetween(employeeId, start, end);
        return sales.stream().map(TicketMapper::toSaleResponse).toList();
    }

    /**
     * 释放已超时的锁座
     *
     * 遍历传入的票列表，将锁座时间超过 lockTimeoutMinutes 的票从 LOCKED 恢复为 AVAILABLE。
     * 该方法在下单前调用，确保超时锁座不会永久占用可售票。
     *
     * @param tickets 待校验的票列表
     */
    private void releaseExpiredLocks(List<Ticket> tickets) {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(lockTimeoutMinutes);
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.LOCKED
                && ticket.getLockTime() != null
                && ticket.getLockTime().isBefore(expiredBefore)) {
                ticket.setStatus(TicketStatus.AVAILABLE);
                ticket.setLockTime(null);
                ticketRepository.save(ticket);
            }
        }
    }
}
