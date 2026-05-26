package com.ttms.studio;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 演出厅新增和修改请求数据。
 */
public class StudioRequest {

    @NotBlank(message = "不能为空")
    @Size(max = 100, message = "长度不能超过100")
    private String name;

    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于等于1")
    private Integer rowCount;

    @NotNull(message = "不能为空")
    @Min(value = 1, message = "必须大于等于1")
    private Integer colCount;

    @Size(max = 2000, message = "长度不能超过2000")
    private String introduction;

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
}
