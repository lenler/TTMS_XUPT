package com.hantang.ttms.domain;

import jakarta.persistence.*;

/**
 * 座位实体类
 * 对应数据库表 seats
 *
 * 每个座位属于一个演出厅（Studio），由行列坐标唯一标识。
 * 同一演出厅内 (row_no, col_no) 不能重复（唯一约束）。
 *
 * 座位由演出厅创建时自动生成（双层循环 row=1..rowCount, col=1..colCount），
 * 状态为 ACTIVE 时可用于排期生成票。
 */
@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"studio_id", "row_no", "col_no"}))
public class Seat extends BaseEntity {
    /** 座位唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属演出厅（多对一），通过 studio_id 外键关联 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "studio_id")
    private Studio studio;

    /** 座位行号（从 1 开始），与 col_no 共同唯一标识一个座位 */
    @Column(name = "row_no", nullable = false)
    private Integer rowNo;

    /** 座位列号（从 1 开始），与 row_no 共同唯一标识一个座位 */
    @Column(name = "col_no", nullable = false)
    private Integer colNo;

    /** 座位状态：ACTIVE（可用）/ DISABLED（已禁用，不生成票） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    /** 获取座位 ID */
    public Long getId() { return id; }
    /** 设置座位 ID */
    public void setId(Long id) { this.id = id; }
    /** 获取所属演出厅 */
    public Studio getStudio() { return studio; }
    /** 设置所属演出厅 */
    public void setStudio(Studio studio) { this.studio = studio; }
    /** 获取行号 */
    public Integer getRowNo() { return rowNo; }
    /** 设置行号 */
    public void setRowNo(Integer rowNo) { this.rowNo = rowNo; }
    /** 获取列号 */
    public Integer getColNo() { return colNo; }
    /** 设置列号 */
    public void setColNo(Integer colNo) { this.colNo = colNo; }
    /** 获取座位状态 */
    public Status getStatus() { return status; }
    /** 设置座位状态 */
    public void setStatus(Status status) { this.status = status; }
}
