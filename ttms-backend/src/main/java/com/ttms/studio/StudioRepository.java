package com.ttms.studio;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 演出厅 MyBatis 数据访问接口。
 */
@Mapper
public interface StudioRepository {

    /**
     * 查询启用演出厅的总数。
     *
     * @param keyword 模糊查询关键字
     * @return 总记录数
     */
    long countActive(@Param("keyword") String keyword);

    /**
     * 分页查询启用演出厅。
     *
     * @param keyword 模糊查询关键字
     * @param offset 查询偏移量
     * @param pageSize 每页数量
     * @return 演出厅列表
     */
    List<Studio> selectActivePage(@Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    /**
     * 根据 ID 查询演出厅。
     *
     * @param id 演出厅 ID
     * @return 演出厅实体
     */
    Studio selectById(@Param("id") Long id);

    /**
     * 插入演出厅。
     *
     * @param studio 演出厅实体
     * @return 影响行数
     */
    int insert(Studio studio);

    /**
     * 更新演出厅基础信息。
     *
     * @param studio 演出厅实体
     * @return 影响行数
     */
    int update(Studio studio);

    /**
     * 将演出厅状态改为停用。
     *
     * @param id 演出厅 ID
     * @return 影响行数
     */
    int disableById(@Param("id") Long id);
}
