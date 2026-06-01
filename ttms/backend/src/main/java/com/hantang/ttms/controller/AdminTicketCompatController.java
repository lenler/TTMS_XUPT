package com.hantang.ttms.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.hantang.ttms.service.SaleService;
import com.hantang.ttms.service.TicketService;

@RestController
@RequestMapping("/admin/api")
public class AdminTicketCompatController {
    private final TicketService ticketService;
    private final SaleService saleService;

    public AdminTicketCompatController(TicketService ticketService, SaleService saleService) {
        this.ticketService = ticketService;
        this.saleService = saleService;
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
        return AdminApiResponse.ok(toVerifyResult(checked, "验票通过"));
    }

    @GetMapping("/checks")
    public AdminApiResponse<AdminPageData<AdminCheckRecord>> checks(
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        return AdminApiResponse.ok(AdminPageData.of(List.of(), page, pageSize));
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
        return new AdminSaleView(
            sale.id(),
            sale.employeeId() == null ? "-" : "员工" + sale.employeeId(),
            sale.customerId() == null ? "-" : "顾客" + sale.customerId(),
            sale.saleTime(),
            sale.paidAmount(),
            sale.changeAmount(),
            sale.saleType() == SaleType.ONLINE ? 1 : 2,
            sale.saleType() == SaleType.ONLINE ? 1 : 2,
            saleStatusCode(sale.status()),
            items
        );
    }

    private AdminVerifyResult toVerifyResult(TicketResponse ticket, String message) {
        return new AdminVerifyResult(
            ticket.id(),
            ticket.rowNo(),
            ticket.colNo(),
            "",
            "",
            LocalDateTime.now(),
            "checked",
            message
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
