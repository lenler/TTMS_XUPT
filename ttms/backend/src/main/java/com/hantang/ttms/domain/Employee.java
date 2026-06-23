package com.hantang.ttms.domain;

import jakarta.persistence.*;

/**
 * 员工实体类
 * 对应数据库表 employees
 *
 * 员工是管理端的操作人员（售票员、管理员等），拥有：
 * - 工号（employee_no，唯一标识）
 * - 职位（售票员 / 管理员等）
 * - 基本个人信息
 *
 * 员工通过管理端登录，负责柜台售票、验票、退票等操作。
 * 工号用于登录认证。
 */
@Entity
@Table(name = "employees", uniqueConstraints = @UniqueConstraint(columnNames = "employee_no"))
public class Employee extends BaseEntity {
    /** 员工唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 工号，唯一，用于管理端登录 */
    @Column(name = "employee_no", nullable = false, length = 20)
    private String employeeNo;

    /** 员工姓名 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 职位，如"售票员"、"管理员" */
    @Column(nullable = false, length = 50)
    private String position;

    /** 联系电话 */
    @Column(length = 30)
    private String phone;

    /** 电子邮箱 */
    @Column(length = 100)
    private String email;

    /** 密码哈希值，使用 BCrypt 加密存储 */
    @Column(nullable = false, length = 100)
    private String passwordHash;

    /** 员工状态：ACTIVE（正常在职）/ DISABLED（已离职或禁用） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    /** 获取员工 ID */
    public Long getId() { return id; }
    /** 设置员工 ID */
    public void setId(Long id) { this.id = id; }
    /** 获取工号 */
    public String getEmployeeNo() { return employeeNo; }
    /** 设置工号 */
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    /** 获取姓名 */
    public String getName() { return name; }
    /** 设置姓名 */
    public void setName(String name) { this.name = name; }
    /** 获取职位 */
    public String getPosition() { return position; }
    /** 设置职位 */
    public void setPosition(String position) { this.position = position; }
    /** 获取联系电话 */
    public String getPhone() { return phone; }
    /** 设置联系电话 */
    public void setPhone(String phone) { this.phone = phone; }
    /** 获取电子邮箱 */
    public String getEmail() { return email; }
    /** 设置电子邮箱 */
    public void setEmail(String email) { this.email = email; }
    /** 获取密码哈希 */
    public String getPasswordHash() { return passwordHash; }
    /** 设置密码哈希 */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    /** 获取员工状态 */
    public Status getStatus() { return status; }
    /** 设置员工状态 */
    public void setStatus(Status status) { this.status = status; }
}
