package com.hantang.ttms.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Sale;
import com.hantang.ttms.domain.SaleItem;
import com.hantang.ttms.domain.SaleStatus;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.FinanceSummaryResponse;
import com.hantang.ttms.repository.SaleRepository;
import com.hantang.ttms.repository.ScheduleRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.FinanceService;

@Service
public class FinanceServiceImpl implements FinanceService {
    private final SaleRepository saleRepository;
    private final TicketRepository ticketRepository;
    private final ScheduleRepository scheduleRepository;

    public FinanceServiceImpl(
        SaleRepository saleRepository,
        TicketRepository ticketRepository,
        ScheduleRepository scheduleRepository
    ) {
        this.saleRepository = saleRepository;
        this.ticketRepository = ticketRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public FinanceSummaryResponse dailySummary(LocalDate date, Long employeeId) {
        LocalDate target = date == null ? LocalDate.now() : date;
        LocalDateTime start = target.atStartOfDay();
        LocalDateTime end = target.plusDays(1).atStartOfDay();
        List<Sale> sales = employeeId == null
            ? saleRepository.findBySaleTimeBetween(start, end)
            : saleRepository.findByEmployeeIdAndSaleTimeBetween(employeeId, start, end);
        return summarizeSales(sales, ticketRepository.countByStatus(TicketStatus.CHECKED), ticketRepository.count());
    }

    @Override
    public FinanceSummaryResponse theaterSummary(LocalDate startDate, LocalDate endDate) {
        LocalDate startDay = startDate == null ? LocalDate.now() : startDate;
        LocalDate endDay = endDate == null ? startDay : endDate;
        List<Sale> sales = saleRepository.findBySaleTimeBetween(startDay.atStartOfDay(), endDay.plusDays(1).atStartOfDay());
        return summarizeSales(sales, ticketRepository.countByStatus(TicketStatus.CHECKED), ticketRepository.count());
    }

    @Override
    public FinanceSummaryResponse scheduleSummary(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new BusinessException("演出计划不存在");
        }
        long total = ticketRepository.countByScheduleId(scheduleId);
        long sold = ticketRepository.countByScheduleIdAndStatus(scheduleId, TicketStatus.SOLD)
            + ticketRepository.countByScheduleIdAndStatus(scheduleId, TicketStatus.CHECKED);
        long checked = ticketRepository.countByScheduleIdAndStatus(scheduleId, TicketStatus.CHECKED);
        BigDecimal amount = ticketRepository.findByScheduleId(scheduleId).stream()
            .filter(ticket -> ticket.getStatus() == TicketStatus.SOLD || ticket.getStatus() == TicketStatus.CHECKED)
            .map(ticket -> ticket.getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new FinanceSummaryResponse(amount, 0, sold, checked, total, rate(checked, total));
    }

    private FinanceSummaryResponse summarizeSales(List<Sale> sales, long checkedTicketCount, long totalTicketCount) {
        List<Sale> paidSales = sales.stream().filter(sale -> sale.getStatus() == SaleStatus.PAID).toList();
        BigDecimal amount = paidSales.stream()
            .flatMap(sale -> sale.getItems().stream())
            .map(SaleItem::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long sold = paidSales.stream().mapToLong(sale -> sale.getItems().size()).sum();
        return new FinanceSummaryResponse(amount, paidSales.size(), sold, checkedTicketCount, totalTicketCount, rate(checkedTicketCount, totalTicketCount));
    }

    private BigDecimal rate(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }
}
