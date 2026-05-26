package com.hantang.ttms.dto;

import java.math.BigDecimal;

import com.hantang.ttms.domain.Sale;
import com.hantang.ttms.domain.SaleItem;
import com.hantang.ttms.domain.Ticket;

public final class TicketMapper {
    private TicketMapper() {}

    public static TicketResponse toTicketResponse(Ticket ticket) {
        return new TicketResponse(
            ticket.getId(),
            ticket.getSchedule().getId(),
            ticket.getSeat().getId(),
            ticket.getSeat().getRowNo(),
            ticket.getSeat().getColNo(),
            ticket.getPrice(),
            ticket.getStatus(),
            ticket.getLockTime()
        );
    }

    public static SaleResponse toSaleResponse(Sale sale) {
        BigDecimal total = sale.getItems().stream()
            .map(SaleItem::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SaleResponse(
            sale.getId(),
            sale.getEmployee() == null ? null : sale.getEmployee().getId(),
            sale.getCustomer() == null ? null : sale.getCustomer().getId(),
            sale.getSaleTime(),
            sale.getPaidAmount(),
            sale.getChangeAmount(),
            sale.getSaleType(),
            sale.getStatus(),
            total,
            sale.getItems().stream().map(SaleItem::getTicket).map(TicketMapper::toTicketResponse).toList()
        );
    }
}
