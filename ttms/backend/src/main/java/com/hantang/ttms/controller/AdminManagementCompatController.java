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

/**
 * 管理端基础数据管理兼容控制器。
 * <p>
 * 为管理后台前端提供演出厅管理、剧目管理、员工管理的完整 CRUD 接口，
 * 以及数据字典（剧目类型、语言、员工职位）查询接口。
 * 路径前缀为 {@code /admin/api}，与前端 Vite 代理规则匹配。
 * </p>
 *
 * <h3>主要功能</h3>
 * <ul>
 *   <li><b>演出厅管理</b>：查询、新增、修改、删除（软删除）演出厅</li>
 *   <li><b>剧目管理</b>：查询（支持关键字、类型、语言过滤）、新增、修改、删除（软删除）剧目</li>
 *   <li><b>员工管理</b>：查询（支持关键字、职位过滤）、新增、修改、删除（软删除）员工</li>
 *   <li><b>数据字典</b>：返回剧目类型、语言、员工职位的静态字典列表</li>
 * </ul>
 *
 * <p>
 * 本控制器作为前端请求的兼容适配层，将前端 flat 字段结构
 * 转换为后端 Service 层期望的请求对象。
 * </p>
 *
 * @author TTMS 开发团队
 * @see StudioService
 * @see PlayService
 * @see UserService
 */
@RestController
@RequestMapping("/admin/api")
public class AdminManagementCompatController {
    /** 剧目类型字典（静态，与前端 Mock 保持一致） */
    private static final List<AdminDictItem> PLAY_TYPES = List.of(
        new AdminDictItem(1, "话剧", "话剧"),
        new AdminDictItem(2, "音乐剧", "音乐剧"),
        new AdminDictItem(3, "戏曲", "戏曲"),
        new AdminDictItem(4, "儿童剧", "儿童剧")
    );
    /** 语言字典（静态，与前端 Mock 保持一致） */
    private static final List<AdminDictItem> LANGUAGES = List.of(
        new AdminDictItem(1, "中文", "中文"),
        new AdminDictItem(2, "英文", "英文"),
        new AdminDictItem(3, "粤语", "粤语")
    );
    /** 员工职位字典（静态，与前端 Mock 保持一致） */
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

    /**
     * 通过构造器注入演出厅、剧目、用户相关服务。
     *
     * @param studioService 演出厅业务逻辑服务
     * @param playService   剧目业务逻辑服务
     * @param userService   用户业务逻辑服务
     */
    public AdminManagementCompatController(StudioService studioService, PlayService playService, UserService userService) {
        this.studioService = studioService;
        this.playService = playService;
        this.userService = userService;
    }

    // ==================== 演出厅管理 ====================

    /**
     * 查询演出厅列表。
     * <p>
     * 支持按名称关键字模糊搜索，返回分页结果。
     * </p>
     *
     * @param keyword  搜索关键字（可选）
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     * @return 分页的演出厅视图列表
     */
    @GetMapping("/studios")
    public AdminApiResponse<AdminPageData<AdminStudioView>> listStudios(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        List<AdminStudioView> studios = studioService.search(keyword).stream().map(this::toAdminStudio).toList();
        return AdminApiResponse.ok(AdminPageData.of(studios, page, pageSize));
    }

    /**
     * 查询单个演出厅详情。
     *
     * @param id 演出厅 ID（路径参数）
     * @return 演出厅详情视图，若不存在则抛出 {@link BusinessException}
     */
    @GetMapping("/studios/{id}")
    public AdminApiResponse<AdminStudioView> getStudio(@PathVariable Long id) {
        return studioService.search(null).stream()
            .filter(studio -> studio.getId().equals(id))
            .findFirst()
            .map(this::toAdminStudio)
            .map(AdminApiResponse::ok)
            .orElseThrow(() -> new BusinessException("演出厅不存在"));
    }

    /**
     * 新增演出厅。
     * <p>
     * 创建时指定演出厅名称、座位排数（rowCount）、每排座位数（colCount）及简介。
     * 创建成功后系统会根据行列数自动生成全部座位。
     * </p>
     *
     * @param request 演出厅创建请求
     * @return 新创建的演出厅 ID
     */
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

    /**
     * 修改演出厅信息。
     * <p>
     * 支持修改名称、行列数、简介。注意：修改行列数不会自动调整已有座位，
     * 如需调整座位布局建议禁用旧演出厅后新建。
     * </p>
     *
     * @param id      演出厅 ID（路径参数）
     * @param request 演出厅更新请求
     * @return 空响应表示操作成功
     */
    @PutMapping("/studios/{id}")
    public AdminApiResponse<Void> updateStudio(@PathVariable Long id, @RequestBody AdminStudioRequest request) {
        studioService.update(id, new StudioRequest(request.name(), request.rowCount(), request.colCount(), request.introduction()));
        return AdminApiResponse.ok(null);
    }

    /**
     * 删除演出厅（软删除，设为禁用状态）。
     * <p>
     * 禁用后该演出厅无法创建新的排期，已有排期不受影响。
     * </p>
     *
     * @param id 演出厅 ID（路径参数）
     * @return 空响应表示操作成功
     */
    @DeleteMapping("/studios/{id}")
    public AdminApiResponse<Void> deleteStudio(@PathVariable Long id) {
        studioService.disable(id);
        return AdminApiResponse.ok(null);
    }

    // ==================== 剧目管理 ====================

    /**
     * 查询剧目列表。
     * <p>
     * 支持按名称关键字模糊搜索，按剧目类型和语言过滤，返回分页结果。
     * </p>
     *
     * @param keyword  搜索关键字（可选）
     * @param type     剧目类型过滤（可选，字典 ID）
     * @param lang     语言过滤（可选，字典 ID）
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     * @return 分页的剧目视图列表
     */
    @GetMapping("/plays")
    public AdminApiResponse<AdminPageData<AdminPlayView>> listPlays(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer type,
        @RequestParam(required = false) Integer lang,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        List<AdminPlayView> plays = playService.search(keyword).stream()
            .filter(play -> type == null || resolveTypeId(play.getType()) == type)
            .filter(play -> lang == null || resolveLangId(play.getLanguage()) == lang)
            .map(this::toAdminPlay)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(plays, page, pageSize));
    }

    /**
     * 查询单个剧目详情。
     *
     * @param id 剧目 ID（路径参数）
     * @return 剧目详情视图，若不存在则抛出 {@link BusinessException}
     */
    @GetMapping("/plays/{id}")
    public AdminApiResponse<AdminPlayView> getPlay(@PathVariable Long id) {
        return playService.search(null).stream()
            .filter(play -> play.getId().equals(id))
            .findFirst()
            .map(this::toAdminPlay)
            .map(AdminApiResponse::ok)
            .orElseThrow(() -> new BusinessException("剧目不存在"));
    }

    /**
     * 新增剧目。
     * <p>
     * 根据前端传来的类型 ID 和语言 ID 查找对应的字典名称，构建剧目领域对象后创建。
     * 包含剧目名称、简介、海报 URL、预告片 URL、时长、基础票价等信息。
     * </p>
     *
     * @param request 剧目创建请求
     * @return 新创建的剧目 ID
     */
    @PostMapping("/plays")
    public AdminApiResponse<AdminIdResponse> createPlay(@RequestBody AdminPlayRequest request) {
        Play saved = playService.create(toPlayRequest(request));
        return AdminApiResponse.ok(new AdminIdResponse(saved.getId()));
    }

    /**
     * 修改剧目信息。
     * <p>
     * 支持修改剧目的全部字段，包括类型、语言、名称、简介、海报、视频、时长、票价。
     * </p>
     *
     * @param id      剧目 ID（路径参数）
     * @param request 剧目更新请求
     * @return 空响应表示操作成功
     */
    @PutMapping("/plays/{id}")
    public AdminApiResponse<Void> updatePlay(@PathVariable Long id, @RequestBody AdminPlayRequest request) {
        playService.update(id, toPlayRequest(request));
        return AdminApiResponse.ok(null);
    }

    /**
     * 删除剧目（软删除，设为禁用状态）。
     * <p>
     * 禁用后该剧目无法创建新的排期，已有排期不受影响。
     * </p>
     *
     * @param id 剧目 ID（路径参数）
     * @return 空响应表示操作成功
     */
    @DeleteMapping("/plays/{id}")
    public AdminApiResponse<Void> deletePlay(@PathVariable Long id) {
        playService.disable(id);
        return AdminApiResponse.ok(null);
    }

    // ==================== 数据字典 ====================

    /**
     * 查询数据字典。
     * <p>
     * 根据 parentId 返回对应的字典项列表：
     * <ul>
     *   <li>{@code type} —— 剧目类型（话剧、音乐剧、戏曲、儿童剧）</li>
     *   <li>{@code lang} —— 语言（中文、英文、粤语）</li>
     *   <li>{@code position} —— 员工职位（售票员、运营经理、系统管理员等）</li>
     * </ul>
     * 字典数据为静态常量，与前端 Mock 保持一致。
     * </p>
     *
     * @param parentId 字典父级标识（必填）：type / lang / position
     * @return 字典响应，包含字典项列表
     */
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

    // ==================== 员工管理 ====================

    /**
     * 查询员工列表。
     * <p>
     * 支持按姓名关键字模糊搜索，按职位过滤，返回分页结果。
     * </p>
     *
     * @param keyword  搜索关键字（可选）
     * @param role     职位过滤（可选，字典 ID）
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     * @return 分页的员工视图列表
     */
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

    /**
     * 查询单个员工详情。
     *
     * @param id 员工 ID（路径参数）
     * @return 员工详情视图，若不存在则抛出 {@link BusinessException}
     */
    @GetMapping("/employees/{id}")
    public AdminApiResponse<AdminEmployeeView> getEmployee(@PathVariable Long id) {
        return userService.listEmployees(null).stream()
            .filter(employee -> employee.id().equals(id))
            .findFirst()
            .map(this::toAdminEmployee)
            .map(AdminApiResponse::ok)
            .orElseThrow(() -> new BusinessException("员工不存在"));
    }

    /**
     * 新增员工。
     * <p>
     * 创建员工账号，包含工号、姓名、职位、手机号、邮箱和登录密码。
     * 职位通过字典 ID 映射为职位名称后传递到服务层。
     * </p>
     *
     * @param request 员工创建请求
     * @return 新创建的员工 ID
     */
    @PostMapping("/employees")
    public AdminApiResponse<AdminIdResponse> createEmployee(@RequestBody AdminEmployeeRequest request) {
        UserResponse saved = userService.createEmployee(toEmployeeRequest(request));
        return AdminApiResponse.ok(new AdminIdResponse(saved.id()));
    }

    /**
     * 修改员工信息。
     * <p>
     * 支持修改工号、姓名、职位、手机号、邮箱、密码等全部字段。
     * </p>
     *
     * @param id      员工 ID（路径参数）
     * @param request 员工更新请求
     * @return 空响应表示操作成功
     */
    @PutMapping("/employees/{id}")
    public AdminApiResponse<Void> updateEmployee(@PathVariable Long id, @RequestBody AdminEmployeeRequest request) {
        userService.updateEmployee(id, toEmployeeRequest(request));
        return AdminApiResponse.ok(null);
    }

    /**
     * 删除员工（软删除，设为禁用状态）。
     * <p>
     * 禁用后该员工无法登录管理后台。
     * </p>
     *
     * @param id 员工 ID（路径参数）
     * @return 空响应表示操作成功
     */
    @DeleteMapping("/employees/{id}")
    public AdminApiResponse<Void> deleteEmployee(@PathVariable Long id) {
        userService.disableEmployee(id);
        return AdminApiResponse.ok(null);
    }

    // ==================== 私有转换方法 ====================

    /**
     * 将演出厅实体转换为管理端演出厅视图。
     *
     * @param studio 演出厅实体
     * @return 管理端演出厅视图
     */
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

    /**
     * 将剧目实体转换为管理端剧目视图。
     * <p>
     * 同时解析类型和语言的字典 ID 与名称，供前端下拉框回显使用。
     * </p>
     *
     * @param play 剧目实体
     * @return 管理端剧目视图
     */
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

    /**
     * 将用户响应 DTO 转换为管理端员工视图。
     * <p>
     * 解析职位字典 ID 与名称，以便前端列表和表单回显。
     * </p>
     *
     * @param employee 用户响应 DTO
     * @return 管理端员工视图
     */
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

    /**
     * 将管理端剧目请求转换为服务层剧目请求。
     * <p>
     * 将前端传递的类型 ID、语言 ID 映射为对应的字典名称，
     * 同时将前端字段名映射到服务层字段名。
     * </p>
     *
     * @param request 管理端剧目请求
     * @return 服务层剧目请求
     */
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

    /**
     * 将管理端员工请求转换为服务层员工请求。
     * <p>
     * 将前端传递的职位 ID 映射为对应的职位名称。
     * </p>
     *
     * @param request 管理端员工请求
     * @return 服务层员工请求
     */
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

    /**
     * 将状态枚举转换为前端数字编码。
     *
     * @param status 状态枚举
     * @return 1（启用）或 0（禁用）
     */
    private int statusCode(Status status) {
        return status == Status.ACTIVE ? 1 : 0;
    }

    /**
     * 根据剧目类型名称解析对应的字典 ID。
     *
     * @param value 类型名称（如"话剧"、"音乐剧"）
     * @return 字典 ID，未匹配则返回 0
     */
    private int resolveTypeId(String value) {
        return resolveDictId(PLAY_TYPES, value);
    }

    /**
     * 根据语言名称解析对应的字典 ID。
     *
     * @param value 语言名称（如"中文"、"英文"）
     * @return 字典 ID，未匹配则返回 0
     */
    private int resolveLangId(String value) {
        return resolveDictId(LANGUAGES, value);
    }

    /**
     * 根据职位名称解析对应的字典 ID。
     *
     * @param value 职位名称（如"售票员"、"系统管理员"）
     * @return 字典 ID，未匹配则返回 0
     */
    private int resolvePositionId(String value) {
        return resolveDictId(POSITIONS, value);
    }

    /**
     * 在指定字典列表中根据名称或值匹配字典项 ID。
     *
     * @param items 字典项列表
     * @param value 要匹配的名称或值
     * @return 匹配的字典项 ID，未匹配则返回 0
     */
    private int resolveDictId(List<AdminDictItem> items, String value) {
        return items.stream()
            .filter(item -> item.value().equals(value) || item.name().equals(value))
            .map(AdminDictItem::id)
            .findFirst()
            .orElse(0);
    }

    /**
     * 根据字典 ID 查找对应的字典项名称。
     *
     * @param items 字典项列表
     * @param id    字典项 ID
     * @return 字典项名称，未匹配则返回空字符串
     */
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
