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

@Service
public class SaleServiceImpl implements SaleService {
    private final TicketRepository ticketRepository;
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final long lockTimeoutMinutes;

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

    @Override
    @Transactional
    public SaleResponse placeOrder(OrderRequest request) {
        List<Ticket> tickets = ticketRepository.findByIdIn(request.ticketIds());
        if (tickets.size() != request.ticketIds().size()) {
            throw new BusinessException("部分票据不存在");
        }

        releaseExpiredLocks(tickets);
        Sale sale = new Sale();
        sale.setSaleType(request.employeeId() == null ? SaleType.ONLINE : SaleType.COUNTER);
        if (request.customerId() != null) {
            Customer customer = customerRepository.findById(request.customerId()).orElseThrow(() -> new BusinessException("顾客不存在"));
            sale.setCustomer(customer);
        }
        if (request.employeeId() != null) {
            Employee employee = employeeRepository.findById(request.employeeId()).orElseThrow(() -> new BusinessException("员工不存在"));
            sale.setEmployee(employee);
        }

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

    @Override
    @Transactional
    public SaleResponse makePayment(Long saleId, BigDecimal paidAmount) {
        Sale sale = saleRepository.findWithItemsById(saleId).orElseThrow(() -> new BusinessException("订单不存在"));
        if (sale.getStatus() != SaleStatus.PENDING_PAYMENT) {
            throw new BusinessException("订单状态不允许支付");
        }

        BigDecimal total = sale.getItems().stream()
            .map(SaleItem::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (paidAmount.compareTo(total) < 0) {
            throw new BusinessException("支付金额不足");
        }

        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(lockTimeoutMinutes);
        for (SaleItem item : sale.getItems()) {
            Ticket ticket = item.getTicket();
            if (ticket.getStatus() != TicketStatus.LOCKED || ticket.getLockTime() == null || ticket.getLockTime().isBefore(expiredBefore)) {
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

    @Override
    @Transactional
    public SaleResponse refund(Long saleId) {
        Sale sale = saleRepository.findWithItemsById(saleId).orElseThrow(() -> new BusinessException("订单不存在"));
        if (sale.getStatus() != SaleStatus.PAID) {
            throw new BusinessException("订单状态不允许退票");
        }
        for (SaleItem item : sale.getItems()) {
            Ticket ticket = item.getTicket();
            if (ticket.getStatus() == TicketStatus.CHECKED) {
                throw new BusinessException("已验票不能退票");
            }
            ticket.setStatus(TicketStatus.REFUNDED);
            ticket.setLockTime(null);
            ticketRepository.save(ticket);
        }
        sale.setStatus(SaleStatus.REFUNDED);
        return TicketMapper.toSaleResponse(saleRepository.save(sale));
    }

    @Override
    public SaleResponse get(Long saleId) {
        Sale sale = saleRepository.findWithItemsById(saleId).orElseThrow(() -> new BusinessException("订单不存在"));
        return TicketMapper.toSaleResponse(sale);
    }

    @Override
    public List<SaleResponse> list(LocalDate date, Long employeeId, Long customerId) {
        if (customerId != null) {
            return saleRepository.findByCustomerId(customerId).stream().map(TicketMapper::toSaleResponse).toList();
        }
        LocalDate target = date == null ? LocalDate.now() : date;
        LocalDateTime start = target.atStartOfDay();
        LocalDateTime end = target.plusDays(1).atStartOfDay();
        List<Sale> sales = employeeId == null
            ? saleRepository.findBySaleTimeBetween(start, end)
            : saleRepository.findByEmployeeIdAndSaleTimeBetween(employeeId, start, end);
        return sales.stream().map(TicketMapper::toSaleResponse).toList();
    }

    private void releaseExpiredLocks(List<Ticket> tickets) {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(lockTimeoutMinutes);
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.LOCKED && ticket.getLockTime() != null && ticket.getLockTime().isBefore(expiredBefore)) {
                ticket.setStatus(TicketStatus.AVAILABLE);
                ticket.setLockTime(null);
                ticketRepository.save(ticket);
            }
        }
    }
}
