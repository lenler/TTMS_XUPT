package com.hantang.ttms.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.domain.SaleStatus;
import com.hantang.ttms.domain.SaleType;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AdminCheckRecord;
import com.hantang.ttms.dto.AdminCreateSaleRequest;
import com.hantang.ttms.dto.AdminCreateSaleResult;
import com.hantang.ttms.dto.AdminPageData;
import com.hantang.ttms.dto.AdminRefundRequest;
import com.hantang.ttms.dto.AdminRefundResult;
import com.hantang.ttms.dto.AdminSaleItemView;
import com.hantang.ttms.dto.AdminSaleView;
import com.hantang.ttms.dto.AdminTicketView;
import com.hantang.ttms.dto.AdminVerifyResult;
import com.hantang.ttms.dto.OrderRequest;
import com.hantang.ttms.dto.SaleResponse;
import com.hantang.ttms.dto.TicketResponse;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.repository.CustomerRepository;
import com.hantang.ttms.repository.EmployeeRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.SaleService;
import com.hantang.ttms.service.TicketService;

@RestController
@RequestMapping("/admin/api")
public class AdminTicketCompatController {
    private final TicketService ticketService;
    private final SaleService saleService;
    private final TicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    /** 内存验票记录（联调阶段，后续接入持久化） */
    private final List<AdminCheckRecord> checkRecords = new ArrayList<>();

    public AdminTicketCompatController(
        TicketService ticketService,
        SaleService saleService,
        TicketRepository ticketRepository,
        EmployeeRepository employeeRepository,
        CustomerRepository customerRepository
    ) {
        this.ticketService = ticketService;
        this.saleService = saleService;
        this.ticketRepository = ticketRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/schedules/{scheduleId}/tickets")
    public AdminApiResponse<AdminPageData<AdminTicketView>> listScheduleTickets(
        @PathVariable Long scheduleId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "500") int pageSize,
        @RequestParam(required = false) Integer status
    ) {
        List<AdminTicketView> tickets = ticketService.listBySchedule(scheduleId).stream()
            .filter(ticket -> status == null || ticketStatusCode(ticket.status()) == status)
            .map(this::toAdminTicket)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(tickets, page, pageSize));
    }

    @PostMapping("/sales")
    public AdminApiResponse<AdminCreateSaleResult> createSale(@Valid @RequestBody AdminCreateSaleRequest request) {
        SaleResponse order = saleService.placeOrder(new OrderRequest(request.customerId(), null, request.ticketIds()));
        SaleResponse paid = saleService.makePayment(order.id(), request.paymentAmount());
        return AdminApiResponse.ok(new AdminCreateSaleResult(paid.id(), paid.changeAmount()));
    }

    @GetMapping("/sales")
    public AdminApiResponse<AdminPageData<AdminSaleView>> listSales(
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        List<AdminSaleView> sales = saleService.list(date, null, null).stream()
            .map(this::toAdminSale)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(sales, page, pageSize));
    }

    @GetMapping("/sales/{id}")
    public AdminApiResponse<AdminSaleView> getSale(@PathVariable Long id) {
        return AdminApiResponse.ok(toAdminSale(saleService.get(id)));
    }

    @PostMapping("/sales/{id}/refund")
    public AdminApiResponse<AdminRefundResult> refundSale(
        @PathVariable Long id,
        @RequestBody(required = false) AdminRefundRequest request
    ) {
        SaleResponse refunded = saleService.refund(id);
        List<Long> ticketIds = refunded.tickets().stream().map(TicketResponse::id).toList();
        BigDecimal amount = refunded.totalAmount();
        return AdminApiResponse.ok(new AdminRefundResult(refunded.id(), ticketIds, amount, "REFUNDED", "REFUNDED"));
    }

    @PostMapping("/tickets/{ticketId}/verify")
    public AdminApiResponse<AdminVerifyResult> verifyTicket(@PathVariable Long ticketId) {
        TicketResponse checked = ticketService.checkIn(ticketId);
        // 从 Repository 查询完整票据信息（含排期→剧目→演出厅）
        Ticket ticket = ticketRepository.findDetailedById(ticketId).orElse(null);
        AdminVerifyResult result = toVerifyResult(checked, ticket, "验票通过");
        // 存入内存验票记录
        if (ticket != null) {
            checkRecords.add(toCheckRecord(ticket, result.showTime()));
        }
        return AdminApiResponse.ok(result);
    }

    @GetMapping("/checks")
    public AdminApiResponse<AdminPageData<AdminCheckRecord>> checks(
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        // 从内存返回验票记录
        return AdminApiResponse.ok(AdminPageData.of(new ArrayList<>(checkRecords), page, pageSize));
    }

    private AdminTicketView toAdminTicket(TicketResponse ticket) {
        return new AdminTicketView(
            ticket.id(),
            ticket.seatId(),
            ticket.rowNo(),
            ticket.colNo(),
            ticket.price(),
            ticketStatusCode(ticket.status()),
            ticket.lockTime()
        );
    }

    private AdminSaleView toAdminSale(SaleResponse sale) {
        List<AdminSaleItemView> items = sale.tickets().stream()
            .map(ticket -> new AdminSaleItemView(ticket.id(), ticket.id(), ticket.rowNo(), ticket.colNo(), ticket.price()))
            .toList();
        // 查询真实姓名
        String employeeName = sale.employeeId() == null ? "-" :
            employeeRepository.findById(sale.employeeId()).map(e -> e.getName()).orElse("员工" + sale.employeeId());
        String customerName = sale.customerId() == null ? "-" :
            customerRepository.findById(sale.customerId()).map(c -> c.getName()).orElse("顾客" + sale.customerId());
        return new AdminSaleView(
            sale.id(),
            employeeName,
            customerName,
            sale.saleTime(),
            sale.paidAmount(),
            sale.changeAmount(),
            sale.saleType() == SaleType.ONLINE ? 1 : 2,
            sale.saleType() == SaleType.ONLINE ? 1 : 2,
            saleStatusCode(sale.status()),
            items
        );
    }

    /** 构建验票结果，从完整 Ticket 实体中提取排期/剧目/演出厅信息 */
    private AdminVerifyResult toVerifyResult(TicketResponse ticket, Ticket fullTicket, String message) {
        String playName = "";
        String studioName = "";
        LocalDateTime showTime = LocalDateTime.now();
        if (fullTicket != null && fullTicket.getSchedule() != null) {
            showTime = fullTicket.getSchedule().getShowTime();
            if (fullTicket.getSchedule().getPlay() != null) {
                playName = fullTicket.getSchedule().getPlay().getName();
            }
            if (fullTicket.getSchedule().getStudio() != null) {
                studioName = fullTicket.getSchedule().getStudio().getName();
            }
        }
        return new AdminVerifyResult(
            ticket.id(),
            ticket.rowNo(),
            ticket.colNo(),
            playName,
            studioName,
            showTime,
            "checked",
            message
        );
    }

    /** 将已验票的票据转为验票记录 */
    private AdminCheckRecord toCheckRecord(Ticket ticket, LocalDateTime verifyTime) {
        String playName = ticket.getSchedule() != null && ticket.getSchedule().getPlay() != null
            ? ticket.getSchedule().getPlay().getName() : "";
        String studioName = ticket.getSchedule() != null && ticket.getSchedule().getStudio() != null
            ? ticket.getSchedule().getStudio().getName() : "";
        return new AdminCheckRecord(
            null,          // id（内存记录无需ID）
            ticket.getId(),
            ticket.getSeat().getRowNo(),
            ticket.getSeat().getColNo(),
            playName,
            studioName,
            ticket.getSchedule() != null ? ticket.getSchedule().getShowTime() : verifyTime,
            verifyTime,
            "系统管理员",   // 操作员
            "通过"          // 验票结果
        );
    }

    private int ticketStatusCode(TicketStatus status) {
        return switch (status) {
            case AVAILABLE -> 0;
            case LOCKED -> 1;
            case SOLD -> 2;
            case CHECKED -> 3;
            case REFUNDED -> 5;
            case VOIDED -> 5;
        };
    }

    private int saleStatusCode(SaleStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> 0;
            case PAID -> 1;
            case REFUNDED -> 3;
            case CANCELLED -> 4;
        };
    }
}
