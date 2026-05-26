package com.ttms.studio;

import com.ttms.common.ApiResponse;
import com.ttms.common.PageResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演出厅管理接口，提供演出厅的查询、新增、修改和停用能力。
 */
@Validated
@RestController
@RequestMapping("/admin/api/studios")
public class StudioController {

    private final StudioService studioService;

    /**
     * 创建演出厅接口控制器。
     *
     * @param studioService 演出厅业务服务
     */
    public StudioController(StudioService studioService) {
        this.studioService = studioService;
    }

    /**
     * 分页查询启用状态的演出厅列表。
     *
     * @param keyword 模糊查询关键字
     * @param page 当前页码
     * @param pageSize 每页数量
     * @return 演出厅分页数据
     */
    @GetMapping
    public ApiResponse<PageResult<Studio>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "必须大于等于1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "必须大于等于1") int pageSize) {
        return ApiResponse.success(studioService.list(keyword, page, pageSize));
    }

    /**
     * 根据 ID 查询单个启用状态的演出厅。
     *
     * @param id 演出厅 ID
     * @return 演出厅详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Studio> getById(@PathVariable Long id) {
        return ApiResponse.success(studioService.getById(id));
    }

    /**
     * 新增演出厅。
     *
     * @param request 演出厅请求数据
     * @return 新增演出厅 ID
     */
    @PostMapping
    public ApiResponse<StudioCreateResponse> create(@Valid @RequestBody StudioRequest request) {
        Long id = studioService.create(request);
        return ApiResponse.success(new StudioCreateResponse(id));
    }

    /**
     * 修改演出厅基础信息。
     *
     * @param id 演出厅 ID
     * @param request 演出厅请求数据
     * @return 空响应数据
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody StudioRequest request) {
        studioService.update(id, request);
        return ApiResponse.success(null);
    }

    /**
     * 停用演出厅。
     *
     * @param id 演出厅 ID
     * @return 空响应数据
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> disable(@PathVariable Long id) {
        studioService.disable(id);
        return ApiResponse.success(null);
    }
}
