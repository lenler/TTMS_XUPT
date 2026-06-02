package com.hantang.ttms.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.domain.Play;
import com.hantang.ttms.domain.Sale;
import com.hantang.ttms.domain.Schedule;
import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AdminPageData;
import com.hantang.ttms.repository.PlayRepository;
import com.hantang.ttms.repository.SaleRepository;
import com.hantang.ttms.repository.ScheduleRepository;
import com.hantang.ttms.repository.StudioRepository;
import com.hantang.ttms.repository.TicketRepository;

/**
 * 管理端财务统计兼容控制器
 * 联调阶段通过直接查询 Repository 计算统计数据
 */
@RestController
@RequestMapping("/admin/api/finance")
public class AdminFinanceCompatController {

    private final TicketRepository ticketRepository;
    private final SaleRepository saleRepository;
    private final ScheduleRepository scheduleRepository;
    private final PlayRepository playRepository;
    private final StudioRepository studioRepository;

    public AdminFinanceCompatController(
        TicketRepository ticketRepository,
        SaleRepository saleRepository,
        ScheduleRepository scheduleRepository,
        PlayRepository playRepository,
        StudioRepository studioRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.saleRepository = saleRepository;
        this.scheduleRepository = scheduleRepository;
        this.playRepository = playRepository;
        this.studioRepository = studioRepository;
    }

    /** 财务概览 */
    @GetMapping("/overview")
    public AdminApiResponse<Map<String, Object>> overview(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate
    ) {
        List<Ticket> tickets = getAllTickets();
        List<Schedule> schedules = scheduleRepository.findAll();
        List<Sale> sales = allSales();

        // 按日期筛选
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        long soldCount = 0;
        BigDecimal totalSales = BigDecimal.ZERO;
        long totalTickets = 0;
        Map<Long, BigDecimal> playSales = new HashMap<>();

        for (Ticket t : tickets) {
            Schedule s = findSchedule(schedules, t);
            if (s == null) continue;
            if (!inDateRange(s.getShowTime().toLocalDate(), start, end)) continue;
            totalTickets++;
            if (t.getStatus() == TicketStatus.SOLD || t.getStatus() == TicketStatus.CHECKED) {
                soldCount++;
                totalSales = totalSales.add(t.getPrice());
                playSales.merge(s.getPlay().getId(), t.getPrice(), BigDecimal::add);
            }
        }

        double avgOccupancy = totalTickets > 0 ? (double) soldCount / totalTickets : 0;

        // 热卖剧目
        Map<String, Object> topPlay = null;
        BigDecimal maxSales = BigDecimal.ZERO;
        for (var entry : playSales.entrySet()) {
            if (entry.getValue().compareTo(maxSales) > 0) {
                maxSales = entry.getValue();
                Play play = findPlay(schedules, entry.getKey());
                topPlay = Map.of(
                    "playId", entry.getKey(),
                    "playName", play != null ? play.getName() : "未知",
                    "sales", entry.getValue().doubleValue()
                );
            }
        }

        return AdminApiResponse.ok(Map.of(
            "totalSales", totalSales.doubleValue(),
            "totalOrders", (long) sales.size(),
            "totalTickets", soldCount,
            "avgOccupancy", Math.round(avgOccupancy * 10000) / 10000.0,
            "topPlay", topPlay
        ));
    }

    /** 剧目销售排名 */
    @GetMapping("/play-ranking")
    public AdminApiResponse<AdminPageData<Map<String, Object>>> playRanking(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        List<Ticket> tickets = getAllTickets();
        List<Schedule> schedules = scheduleRepository.findAll();
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        Map<Long, PlayStats> stats = new HashMap<>();
        for (Ticket t : tickets) {
            Schedule s = findSchedule(schedules, t);
            if (s == null) continue;
            if (!inDateRange(s.getShowTime().toLocalDate(), start, end)) continue;
            Long playId = s.getPlay().getId();
            PlayStats ps = stats.computeIfAbsent(playId, k -> new PlayStats());
            ps.showIds.add(s.getId());
            ps.totalTickets++;
            if (t.getStatus() == TicketStatus.SOLD || t.getStatus() == TicketStatus.CHECKED) {
                ps.soldTickets++;
                ps.sales = ps.sales.add(t.getPrice());
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (var entry : stats.entrySet()) {
            Play play = findPlay(schedules, entry.getKey());
            PlayStats ps = entry.getValue();
            double occupancy = ps.totalTickets > 0 ? (double) ps.soldTickets / ps.totalTickets : 0;
            list.add(Map.<String, Object>of(
                "playId", entry.getKey(),
                "playName", play != null ? play.getName() : "未知",
                "showCount", ps.showIds.size(),
                "totalTickets", ps.totalTickets,
                "soldTickets", ps.soldTickets,
                "occupancy", Math.round(occupancy * 10000) / 10000.0,
                "sales", ps.sales.doubleValue()
            ));
        }
        list.sort((a, b) -> Double.compare((Double) b.get("sales"), (Double) a.get("sales")));

        return AdminApiResponse.ok(AdminPageData.of(list, page, pageSize));
    }

    /** 剧院销售业绩 */
    @GetMapping("/studio-performance")
    public AdminApiResponse<Map<String, Object>> studioPerformance(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate
    ) {
        List<Ticket> tickets = getAllTickets();
        List<Schedule> schedules = scheduleRepository.findAll();
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        Map<Long, StudioStats> stats = new HashMap<>();
        for (Ticket t : tickets) {
            Schedule s = findSchedule(schedules, t);
            if (s == null) continue;
            if (!inDateRange(s.getShowTime().toLocalDate(), start, end)) continue;
            Long studioId = s.getStudio().getId();
            StudioStats ss = stats.computeIfAbsent(studioId, k -> new StudioStats());
            ss.showIds.add(s.getId());
            ss.totalSeats++;
            if (t.getStatus() == TicketStatus.SOLD || t.getStatus() == TicketStatus.CHECKED) {
                ss.soldSeats++;
                ss.sales = ss.sales.add(t.getPrice());
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (var entry : stats.entrySet()) {
            Studio studio = findStudio(schedules, entry.getKey());
            StudioStats ss = entry.getValue();
            double occupancy = ss.totalSeats > 0 ? (double) ss.soldSeats / ss.totalSeats : 0;
            list.add(Map.<String, Object>of(
                "studioId", entry.getKey(),
                "studioName", studio != null ? studio.getName() : "未知",
                "showCount", ss.showIds.size(),
                "totalSeats", ss.totalSeats,
                "soldSeats", ss.soldSeats,
                "occupancy", Math.round(occupancy * 10000) / 10000.0,
                "sales", ss.sales.doubleValue()
            ));
        }

        return AdminApiResponse.ok(Map.of("list", list));
    }

    /** 售票员销售统计 */
    @GetMapping("/employee-sales")
    public AdminApiResponse<Map<String, Object>> employeeSales(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate
    ) {
        List<Sale> sales = allSales();
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        // 按员工分组合并
        Map<Long, EmpStats> stats = new HashMap<>();
        for (Sale s : sales) {
            LocalDate saleDate = s.getSaleTime().toLocalDate();
            if (!inDateRange(saleDate, start, end)) continue;

            Long empId = s.getEmployee() != null ? s.getEmployee().getId() : 0L;
            EmpStats es = stats.computeIfAbsent(empId, k -> new EmpStats());
            es.name = s.getEmployee() != null ? s.getEmployee().getName() : "-";
            if (s.getStatus().name().equals("REFUNDED")) {
                es.refundCount++;
                es.refundAmount = es.refundAmount.add(s.getPaidAmount() != null ? s.getPaidAmount() : BigDecimal.ZERO);
            } else {
                es.orderCount++;
                es.totalAmount = es.totalAmount.add(s.getPaidAmount() != null ? s.getPaidAmount() : BigDecimal.ZERO);
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();
        int idx = 1;
        for (var entry : stats.entrySet()) {
            EmpStats es = entry.getValue();
            list.add(Map.<String, Object>of(
                "employeeId", entry.getKey(),
                "employeeName", es.name,
                "orderCount", es.orderCount,
                "totalAmount", es.totalAmount.doubleValue(),
                "refundCount", es.refundCount,
                "refundAmount", es.refundAmount.doubleValue()
            ));
            idx++;
        }

        return AdminApiResponse.ok(Map.of("list", list));
    }

    // ==================== 工具方法 ====================

    private List<Ticket> getAllTickets() {
        // 通过所有排期查询票据
        List<Ticket> all = new ArrayList<>();
        for (Schedule s : scheduleRepository.findAll()) {
            all.addAll(ticketRepository.findByScheduleId(s.getId()));
        }
        return all;
    }

    private Schedule findSchedule(List<Schedule> schedules, Ticket ticket) {
        return schedules.stream().filter(s -> s.getId().equals(ticket.getSchedule().getId())).findFirst().orElse(null);
    }

    private Play findPlay(List<Schedule> schedules, Long playId) {
        return schedules.stream()
            .filter(s -> s.getPlay().getId().equals(playId))
            .map(Schedule::getPlay)
            .findFirst().orElse(null);
    }

    private Studio findStudio(List<Schedule> schedules, Long studioId) {
        return schedules.stream()
            .filter(s -> s.getStudio().getId().equals(studioId))
            .map(Schedule::getStudio)
            .findFirst().orElse(null);
    }

    private List<Sale> allSales() {
        return saleRepository.findBySaleTimeBetween(
            LocalDateTime.of(2020, 1, 1, 0, 0),
            LocalDateTime.of(2030, 1, 1, 0, 0)
        );
    }

    private boolean inDateRange(LocalDate date, LocalDate start, LocalDate end) {
        if (start != null && date.isBefore(start)) return false;
        if (end != null && date.isAfter(end)) return false;
        return true;
    }

    private static class PlayStats {
        Set<Long> showIds = new HashSet<>();
        long totalTickets = 0;
        long soldTickets = 0;
        BigDecimal sales = BigDecimal.ZERO;
    }

    private static class StudioStats {
        Set<Long> showIds = new HashSet<>();
        long totalSeats = 0;
        long soldSeats = 0;
        BigDecimal sales = BigDecimal.ZERO;
    }

    private static class EmpStats {
        String name = "";
        int orderCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        int refundCount = 0;
        BigDecimal refundAmount = BigDecimal.ZERO;
    }
}
