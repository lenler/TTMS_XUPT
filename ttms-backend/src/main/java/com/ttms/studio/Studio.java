package com.ttms.studio;

import java.time.LocalDateTime;

/**
 * 演出厅实体，表示剧院中可安排演出的物理厅室。
 */
public class Studio {

    private Long id;
    private String name;
    private Integer rowCount;
    private Integer colCount;
    private String introduction;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 获取演出厅 ID。
     *
     * @return 演出厅 ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置演出厅 ID。
     *
     * @param id 演出厅 ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取演出厅名称。
     *
     * @return 演出厅名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置演出厅名称。
     *
     * @param name 演出厅名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取座位行数。
     *
     * @return 座位行数
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * 设置座位行数。
     *
     * @param rowCount 座位行数
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 获取座位列数。
     *
     * @return 座位列数
     */
    public Integer getColCount() {
        return colCount;
    }

    /**
     * 设置座位列数。
     *
     * @param colCount 座位列数
     */
    public void setColCount(Integer colCount) {
        this.colCount = colCount;
    }

    /**
     * 获取演出厅简介。
     *
     * @return 演出厅简介
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * 设置演出厅简介。
     *
     * @param introduction 演出厅简介
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    /**
     * 获取演出厅状态。
     *
     * @return 1 表示启用，0 表示停用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置演出厅状态。
     *
     * @param status 1 表示启用，0 表示停用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取创建时间。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间。
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
