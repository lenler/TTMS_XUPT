package com.hantang.ttms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AdminDictItem;
import com.hantang.ttms.dto.AdminDictResponse;
import com.hantang.ttms.dto.AdminEmployeeRequest;
import com.hantang.ttms.dto.AdminEmployeeView;
import com.hantang.ttms.dto.AdminIdResponse;
import com.hantang.ttms.dto.AdminPageData;
import com.hantang.ttms.dto.AdminPlayRequest;
import com.hantang.ttms.dto.AdminPlayView;
import com.hantang.ttms.dto.AdminStudioRequest;
import com.hantang.ttms.dto.AdminStudioView;
import com.hantang.ttms.dto.EmployeeRequest;
import com.hantang.ttms.dto.PlayRequest;
import com.hantang.ttms.dto.StudioRequest;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.service.PlayService;
import com.hantang.ttms.service.StudioService;
import com.hantang.ttms.service.UserService;

@RestController
@RequestMapping("/admin/api")
public class AdminManagementCompatController {
    private static final List<AdminDictItem> PLAY_TYPES = List.of(
        new AdminDictItem(1, "话剧", "话剧"),
        new AdminDictItem(2, "音乐剧", "音乐剧"),
        new AdminDictItem(3, "戏曲", "戏曲"),
        new AdminDictItem(4, "儿童剧", "儿童剧")
    );
    private static final List<AdminDictItem> LANGUAGES = List.of(
        new AdminDictItem(1, "中文", "中文"),
        new AdminDictItem(2, "英文", "英文"),
        new AdminDictItem(3, "粤语", "粤语")
    );
    private static final List<AdminDictItem> POSITIONS = List.of(
        new AdminDictItem(1, "售票员", "售票员"),
        new AdminDictItem(2, "运营经理", "运营经理"),
        new AdminDictItem(3, "系统管理员", "系统管理员"),
        new AdminDictItem(4, "会计", "会计"),
        new AdminDictItem(5, "财务经理", "财务经理"),
        new AdminDictItem(6, "场务员", "场务员"),
        new AdminDictItem(7, "设备运维", "设备运维")
    );

    private final StudioService studioService;
    private final PlayService playService;
    private final UserService userService;

    public AdminManagementCompatController(StudioService studioService, PlayService playService, UserService userService) {
        this.studioService = studioService;
        this.playService = playService;
        this.userService = userService;
    }

    @GetMapping("/studios")
    public AdminApiResponse<AdminPageData<AdminStudioView>> listStudios(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        List<AdminStudioView> studios = studioService.search(keyword).stream().map(this::toAdminStudio).toList();
        return AdminApiResponse.ok(AdminPageData.of(studios, page, pageSize));
    }

    @GetMapping("/studios/{id}")
    public AdminApiResponse<AdminStudioView> getStudio(@PathVariable Long id) {
        return studioService.search(null).stream()
            .filter(studio -> studio.getId().equals(id))
            .findFirst()
            .map(this::toAdminStudio)
            .map(AdminApiResponse::ok)
            .orElseThrow(() -> new BusinessException("演出厅不存在"));
    }

    @PostMapping("/studios")
    public AdminApiResponse<AdminIdResponse> createStudio(@RequestBody AdminStudioRequest request) {
        Studio saved = studioService.create(new StudioRequest(
            request.name(),
            request.rowCount(),
            request.colCount(),
            request.introduction()
        ));
        return AdminApiResponse.ok(new AdminIdResponse(saved.getId()));
    }

    @PutMapping("/studios/{id}")
    public AdminApiResponse<Void> updateStudio(@PathVariable Long id, @RequestBody AdminStudioRequest request) {
        studioService.update(id, new StudioRequest(request.name(), request.rowCount(), request.colCount(), request.introduction()));
        return AdminApiResponse.ok(null);
    }

    @DeleteMapping("/studios/{id}")
    public AdminApiResponse<Void> deleteStudio(@PathVariable Long id) {
        studioService.disable(id);
        return AdminApiResponse.ok(null);
    }

    @GetMapping("/plays")
    public AdminApiResponse<AdminPageData<AdminPlayView>> listPlays(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer type,
        @RequestParam(required = false) Integer lang,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        List<AdminPlayView> plays = playService.search(keyword).stream()
            .filter(play -> play.getStatus() == Status.ACTIVE)
            .filter(play -> type == null || resolveTypeId(play.getType()) == type)
            .filter(play -> lang == null || resolveLangId(play.getLanguage()) == lang)
            .map(this::toAdminPlay)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(plays, page, pageSize));
    }

    @GetMapping("/plays/{id}")
    public AdminApiResponse<AdminPlayView> getPlay(@PathVariable Long id) {
        return playService.search(null).stream()
            .filter(play -> play.getId().equals(id))
            .findFirst()
            .map(this::toAdminPlay)
            .map(AdminApiResponse::ok)
            .orElseThrow(() -> new BusinessException("剧目不存在"));
    }

    @PostMapping("/plays")
    public AdminApiResponse<AdminIdResponse> createPlay(@RequestBody AdminPlayRequest request) {
        Play saved = playService.create(toPlayRequest(request));
        return AdminApiResponse.ok(new AdminIdResponse(saved.getId()));
    }

    @PutMapping("/plays/{id}")
    public AdminApiResponse<Void> updatePlay(@PathVariable Long id, @RequestBody AdminPlayRequest request) {
        playService.update(id, toPlayRequest(request));
        return AdminApiResponse.ok(null);
    }

    @DeleteMapping("/plays/{id}")
    public AdminApiResponse<Void> deletePlay(@PathVariable Long id) {
        playService.disable(id);
        return AdminApiResponse.ok(null);
    }

    @GetMapping("/dicts")
    public AdminApiResponse<AdminDictResponse> getDicts(@RequestParam String parentId) {
        List<AdminDictItem> items = switch (parentId) {
            case "type" -> PLAY_TYPES;
            case "lang" -> LANGUAGES;
            case "position" -> POSITIONS;
            default -> List.of();
        };
        return AdminApiResponse.ok(new AdminDictResponse(items));
    }

    @GetMapping("/employees")
    public AdminApiResponse<AdminPageData<AdminEmployeeView>> listEmployees(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer role,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        List<AdminEmployeeView> employees = userService.listEmployees(keyword).stream()
            .filter(employee -> role == null || resolvePositionId(employee.position()) == role)
            .map(this::toAdminEmployee)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(employees, page, pageSize));
    }

    @GetMapping("/employees/{id}")
    public AdminApiResponse<AdminEmployeeView> getEmployee(@PathVariable Long id) {
        return userService.listEmployees(null).stream()
            .filter(employee -> employee.id().equals(id))
            .findFirst()
            .map(this::toAdminEmployee)
            .map(AdminApiResponse::ok)
            .orElseThrow(() -> new BusinessException("员工不存在"));
    }

    @PostMapping("/employees")
    public AdminApiResponse<AdminIdResponse> createEmployee(@RequestBody AdminEmployeeRequest request) {
        UserResponse saved = userService.createEmployee(toEmployeeRequest(request));
        return AdminApiResponse.ok(new AdminIdResponse(saved.id()));
    }

    @PutMapping("/employees/{id}")
    public AdminApiResponse<Void> updateEmployee(@PathVariable Long id, @RequestBody AdminEmployeeRequest request) {
        userService.updateEmployee(id, toEmployeeRequest(request));
        return AdminApiResponse.ok(null);
    }

    @DeleteMapping("/employees/{id}")
    public AdminApiResponse<Void> deleteEmployee(@PathVariable Long id) {
        userService.disableEmployee(id);
        return AdminApiResponse.ok(null);
    }

    private AdminStudioView toAdminStudio(Studio studio) {
        return new AdminStudioView(
            studio.getId(),
            studio.getName(),
            studio.getRowCount(),
            studio.getColCount(),
            studio.getIntroduction(),
            statusCode(studio.getStatus())
        );
    }

    private AdminPlayView toAdminPlay(Play play) {
        int typeId = resolveTypeId(play.getType());
        int langId = resolveLangId(play.getLanguage());
        return new AdminPlayView(
            play.getId(),
            typeId,
            dictName(PLAY_TYPES, typeId),
            langId,
            dictName(LANGUAGES, langId),
            play.getName(),
            play.getIntroduction(),
            play.getPosterUrl(),
            play.getTrailerUrl(),
            play.getDurationMinutes(),
            play.getBasePrice(),
            statusCode(play.getStatus())
        );
    }

    private AdminEmployeeView toAdminEmployee(UserResponse employee) {
        int positionId = resolvePositionId(employee.position());
        return new AdminEmployeeView(
            employee.id(),
            employee.employeeNo(),
            employee.name(),
            1,
            employee.phone(),
            employee.email(),
            positionId,
            dictName(POSITIONS, positionId),
            statusCode(employee.status())
        );
    }

    private PlayRequest toPlayRequest(AdminPlayRequest request) {
        return new PlayRequest(
            dictName(PLAY_TYPES, request.typeId()),
            dictName(LANGUAGES, request.langId()),
            request.name(),
            request.introduction(),
            request.poster(),
            request.video(),
            request.duration(),
            request.basePrice()
        );
    }

    private EmployeeRequest toEmployeeRequest(AdminEmployeeRequest request) {
        return new EmployeeRequest(
            request.employeeNo(),
            request.name(),
            dictName(POSITIONS, request.positionId()),
            request.phone(),
            request.email(),
            request.password()
        );
    }

    private int statusCode(Status status) {
        return status == Status.ACTIVE ? 1 : 0;
    }

    private int resolveTypeId(String value) {
        return resolveDictId(PLAY_TYPES, value);
    }

    private int resolveLangId(String value) {
        return resolveDictId(LANGUAGES, value);
    }

    private int resolvePositionId(String value) {
        return resolveDictId(POSITIONS, value);
    }

    private int resolveDictId(List<AdminDictItem> items, String value) {
        return items.stream()
            .filter(item -> item.value().equals(value) || item.name().equals(value))
            .map(AdminDictItem::id)
            .findFirst()
            .orElse(0);
    }

    private String dictName(List<AdminDictItem> items, Integer id) {
        if (id == null) {
            return "";
        }
        return items.stream()
            .filter(item -> item.id() == id)
            .map(AdminDictItem::name)
            .findFirst()
            .orElse("");
    }
}
