package com.hantang.ttms.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Customer;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.domain.Schedule;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AdminCustomerView;
import com.hantang.ttms.dto.AdminIdResponse;
import com.hantang.ttms.dto.AdminPageData;
import com.hantang.ttms.dto.AdminScheduleRequest;
import com.hantang.ttms.dto.AdminScheduleView;
import com.hantang.ttms.dto.AdminStatusRequest;
import com.hantang.ttms.dto.ScheduleRequest;
import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.repository.CustomerRepository;
import com.hantang.ttms.repository.PlayRepository;
import com.hantang.ttms.repository.ScheduleRepository;
import com.hantang.ttms.repository.StudioRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.ScheduleService;
import com.hantang.ttms.service.UserService;

@RestController
@RequestMapping("/admin/api")
public class AdminCatalogCompatController {
    private static final DateTimeFormatter FRONT_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final ScheduleRepository scheduleRepository;
    private final StudioRepository studioRepository;
    private final PlayRepository playRepository;
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;

    public AdminCatalogCompatController(
        ScheduleService scheduleService,
        UserService userService,
        ScheduleRepository scheduleRepository,
        StudioRepository studioRepository,
        PlayRepository playRepository,
        TicketRepository ticketRepository,
        CustomerRepository customerRepository
    ) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.scheduleRepository = scheduleRepository;
        this.studioRepository = studioRepository;
        this.playRepository = playRepository;
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/schedules")
    public AdminApiResponse<AdminPageData<AdminScheduleView>> listSchedules(
        @RequestParam(required = false) Long playId,
        @RequestParam(required = false) Long studioId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "100") int pageSize
    ) {
        List<AdminScheduleView> schedules = scheduleService.listByPlay(playId).stream()
            .filter(schedule -> studioId == null || schedule.studioId().equals(studioId))
            .map(this::toAdminSchedule)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(schedules, page, pageSize));
    }

    @GetMapping("/schedules/{id}")
    public AdminApiResponse<AdminScheduleView> getSchedule(@PathVariable Long id) {
        return scheduleService.listByPlay(null).stream()
            .filter(schedule -> schedule.id().equals(id))
            .findFirst()
            .map(this::toAdminSchedule)
            .map(AdminApiResponse::ok)
            .orElseThrow(() -> new BusinessException("演出计划不存在"));
    }

    @PostMapping("/schedules")
    public AdminApiResponse<AdminIdResponse> createSchedule(@RequestBody AdminScheduleRequest request) {
        ScheduleResponse saved = scheduleService.create(new ScheduleRequest(
            request.studioId(),
            request.playId(),
            parseShowTime(request.showTime()),
            request.ticketPrice()
        ));
        return AdminApiResponse.ok(new AdminIdResponse(saved.id()));
    }

    @PutMapping("/schedules/{id}")
    @Transactional
    public AdminApiResponse<Void> updateSchedule(@PathVariable Long id, @RequestBody AdminScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new BusinessException("演出计划不存在"));
        Studio studio = studioRepository.findById(request.studioId()).orElseThrow(() -> new BusinessException("演出厅不存在"));
        Play play = playRepository.findById(request.playId()).orElseThrow(() -> new BusinessException("剧目不存在"));
        LocalDateTime showTime = parseShowTime(request.showTime());
        if (hasConflict(id, studio.getId(), showTime, play.getDurationMinutes())) {
            throw new BusinessException("演出厅该时段已被占用");
        }
        schedule.setStudio(studio);
        schedule.setPlay(play);
        schedule.setShowTime(showTime);
        schedule.setTicketPrice(request.ticketPrice());
        scheduleRepository.save(schedule);
        for (Ticket ticket : ticketRepository.findByScheduleId(id)) {
            if (ticket.getStatus() == TicketStatus.AVAILABLE) {
                ticket.setPrice(request.ticketPrice());
            }
        }
        return AdminApiResponse.ok(null);
    }

    @DeleteMapping("/schedules/{id}")
    @Transactional
    public AdminApiResponse<Void> deleteSchedule(@PathVariable Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new BusinessException("演出计划不存在"));
        schedule.setStatus(Status.DISABLED);
        scheduleRepository.save(schedule);
        return AdminApiResponse.ok(null);
    }

    @GetMapping("/customers")
    public AdminApiResponse<AdminPageData<AdminCustomerView>> listCustomers(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "100") int pageSize
    ) {
        List<AdminCustomerView> customers = userService.listCustomers(keyword).stream()
            .map(this::toAdminCustomer)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(customers, page, pageSize));
    }

    @GetMapping("/customers/{id}")
    public AdminApiResponse<AdminCustomerView> getCustomer(@PathVariable Long id) {
        return userService.listCustomers(null).stream()
            .filter(customer -> customer.id().equals(id))
            .findFirst()
            .map(this::toAdminCustomer)
            .map(AdminApiResponse::ok)
            .orElseGet(() -> new AdminApiResponse<>("20004", "观众不存在", null));
    }

    @PutMapping("/customers/{id}/status")
    @Transactional
    public AdminApiResponse<Void> updateCustomerStatus(@PathVariable Long id, @RequestBody AdminStatusRequest request) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new BusinessException("观众不存在"));
        customer.setStatus(request.status() != null && request.status() == 1 ? Status.ACTIVE : Status.DISABLED);
        customerRepository.save(customer);
        return AdminApiResponse.ok(null);
    }

    private AdminScheduleView toAdminSchedule(ScheduleResponse schedule) {
        return new AdminScheduleView(
            schedule.id(),
            schedule.playId(),
            schedule.playName(),
            schedule.studioId(),
            schedule.studioName(),
            schedule.showTime(),
            schedule.ticketPrice(),
            statusCode(schedule.status())
        );
    }

    private AdminCustomerView toAdminCustomer(UserResponse customer) {
        return new AdminCustomerView(
            customer.id(),
            customer.name(),
            0,
            customer.phone(),
            customer.email(),
            customer.username(),
            customer.balance(),
            statusCode(customer.status())
        );
    }

    private int statusCode(Status status) {
        return status == Status.ACTIVE ? 1 : 0;
    }

    private LocalDateTime parseShowTime(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException("演出时间不能为空");
        }
        String normalized = value.contains("T") ? value : value.replace(' ', 'T');
        try {
            return LocalDateTime.parse(normalized);
        } catch (RuntimeException ignored) {
            return LocalDateTime.parse(value, FRONT_TIME_FORMAT);
        }
    }

    private boolean hasConflict(Long scheduleId, Long studioId, LocalDateTime showTime, Integer durationMinutes) {
        LocalDateTime newStart = showTime;
        LocalDateTime newEnd = showTime.plusMinutes(durationMinutes);
        return scheduleRepository.findByStudioIdAndStatus(studioId, Status.ACTIVE).stream()
            .filter(existing -> !existing.getId().equals(scheduleId))
            .anyMatch(existing -> {
                LocalDateTime existingStart = existing.getShowTime();
                LocalDateTime existingEnd = existing.getShowTime().plusMinutes(existing.getPlay().getDurationMinutes());
                return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
            });
    }
}
