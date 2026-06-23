package com.hantang.ttms.domain;

import java.math.BigDecimal;

import jakarta.persistence.*;

/**
 * 客户（观众）实体类
 * 对应数据库表 customers
 *
 * 客户是通过观众端注册的普通用户，拥有：
 * - 基本个人信息（用户名、姓名、电话、邮箱、性别）
 * - 账户余额（用于在线购票支付）
 * - 支付密码（用于支付确认）
 *
 * 用户名（username）唯一，用于登录认证。
 * 状态为 DISABLED 时不可登录和购票。
 */
@Entity
@Table(name = "customers", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Customer extends BaseEntity {
    /** 客户唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名，唯一，用于登录和显示 */
    @Column(nullable = false, length = 50)
    private String username;

    /** 密码哈希值，使用 BCrypt 加密存储，不可明文 */
    @Column(nullable = false, length = 100)
    private String passwordHash;

    /** 客户真实姓名 */
    @Column(length = 100)
    private String name;

    /** 联系电话 */
    @Column(length = 30)
    private String phone;

    /** 电子邮箱 */
    @Column(length = 100)
    private String email;

    /** 性别：0=未知 1=男 2=女 */
    @Column(nullable = false)
    private Integer gender = 0;

    /** 支付密码（联调阶段明文存储，生产环境应使用 BCrypt 哈希） */
    @Column(length = 100)
    private String paymentPassword;

    /** 账户余额（元），用于在线购票扣款，充值后增加 */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /** 客户状态：ACTIVE（正常可用）/ DISABLED（已禁用，不可登录购票） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    /** 获取客户 ID */
    public Long getId() { return id; }
    /** 设置客户 ID */
    public void setId(Long id) { this.id = id; }
    /** 获取用户名 */
    public String getUsername() { return username; }
    /** 设置用户名 */
    public void setUsername(String username) { this.username = username; }
    /** 获取密码哈希 */
    public String getPasswordHash() { return passwordHash; }
    /** 设置密码哈希 */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    /** 获取真实姓名 */
    public String getName() { return name; }
    /** 设置真实姓名 */
    public void setName(String name) { this.name = name; }
    /** 获取联系电话 */
    public String getPhone() { return phone; }
    /** 设置联系电话 */
    public void setPhone(String phone) { this.phone = phone; }
    /** 获取电子邮箱 */
    public String getEmail() { return email; }
    /** 设置电子邮箱 */
    public void setEmail(String email) { this.email = email; }
    /** 获取性别（0=未知 1=男 2=女） */
    public Integer getGender() { return gender; }
    /** 设置性别 */
    public void setGender(Integer gender) { this.gender = gender; }
    /** 获取支付密码 */
    public String getPaymentPassword() { return paymentPassword; }
    /** 设置支付密码 */
    public void setPaymentPassword(String paymentPassword) { this.paymentPassword = paymentPassword; }
    /** 获取账户余额 */
    public BigDecimal getBalance() { return balance; }
    /** 设置账户余额 */
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    /** 获取客户状态 */
    public Status getStatus() { return status; }
    /** 设置客户状态 */
    public void setStatus(Status status) { this.status = status; }
}
