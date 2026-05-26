package com.hantang.ttms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.domain.Status;
import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AdminCustomerView;
import com.hantang.ttms.dto.AdminPageData;
import com.hantang.ttms.dto.AdminScheduleView;
import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.service.ScheduleService;
import com.hantang.ttms.service.UserService;

@RestController
@RequestMapping("/admin/api")
public class AdminCatalogCompatController {
    private final ScheduleService scheduleService;
    private final UserService userService;

    public AdminCatalogCompatController(ScheduleService scheduleService, UserService userService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @GetMapping("/schedules")
    public AdminApiResponse<AdminPageData<AdminScheduleView>> listSchedules(
        @RequestParam(required = false) Long playId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "100") int pageSize
    ) {
        List<AdminScheduleView> schedules = scheduleService.listByPlay(playId).stream()
            .map(this::toAdminSchedule)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(schedules, page, pageSize));
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
}
