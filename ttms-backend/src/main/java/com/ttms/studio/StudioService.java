package com.ttms.studio;

import com.ttms.common.PageResult;

/**
 * 演出厅业务服务接口。
 */
public interface StudioService {

    /**
     * 分页查询启用演出厅。
     *
     * @param keyword 模糊查询关键字
     * @param page 当前页码
     * @param pageSize 每页数量
     * @return 演出厅分页数据
     */
    PageResult<Studio> list(String keyword, int page, int pageSize);

    /**
     * 根据 ID 查询启用演出厅。
     *
     * @param id 演出厅 ID
     * @return 演出厅实体
     */
    Studio getById(Long id);

    /**
     * 新增演出厅。
     *
     * @param request 演出厅请求数据
     * @return 新增演出厅 ID
     */
    Long create(StudioRequest request);

    /**
     * 修改演出厅基础信息。
     *
     * @param id 演出厅 ID
     * @param request 演出厅请求数据
     */
    void update(Long id, StudioRequest request);

    /**
     * 停用演出厅。
     *
     * @param id 演出厅 ID
     */
    void disable(Long id);
}
