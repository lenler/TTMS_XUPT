package com.hantang.ttms.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "studios")
public class Studio extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer rowCount;

    @Column(nullable = false)
    private Integer colCount;

    @Column(length = 2000)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getRowCount() { return rowCount; }
    public void setRowCount(Integer rowCount) { this.rowCount = rowCount; }
    public Integer getColCount() { return colCount; }
    public void setColCount(Integer colCount) { this.colCount = colCount; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
