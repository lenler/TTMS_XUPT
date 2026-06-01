package com.hantang.ttms.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"studio_id", "row_no", "col_no"}))
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "studio_id")
    private Studio studio;

    @Column(name = "row_no", nullable = false)
    private Integer rowNo;

    @Column(name = "col_no", nullable = false)
    private Integer colNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Studio getStudio() { return studio; }
    public void setStudio(Studio studio) { this.studio = studio; }
    public Integer getRowNo() { return rowNo; }
    public void setRowNo(Integer rowNo) { this.rowNo = rowNo; }
    public Integer getColNo() { return colNo; }
    public void setColNo(Integer colNo) { this.colNo = colNo; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
