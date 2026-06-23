package com.hantang.ttms.domain;

import jakarta.persistence.*;

/**
 * 角色实体类
 * 对应数据库表 roles
 *
 * 角色用于管理端的权限控制，不同的角色拥有不同的菜单和操作权限。
 * 角色名称（name）唯一。
 *
 * 典型角色：系统管理员、售票员、财务等
 */
@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Role extends BaseEntity {
    /** 角色唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 角色名称，如"系统管理员"、"售票员"，唯一 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 获取角色 ID */
    public Long getId() { return id; }
    /** 设置角色 ID */
    public void setId(Long id) { this.id = id; }
    /** 获取角色名称 */
    public String getName() { return name; }
    /** 设置角色名称 */
    public void setName(String name) { this.name = name; }
}
