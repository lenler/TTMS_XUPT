package com.hantang.ttms.domain;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "customers", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(length = 100)
    private String name;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String email;

    /** 性别：0=未知 1=男 2=女 */
    @Column(nullable = false)
    private Integer gender = 0;

    /** 支付密码（联调阶段明文存储） */
    @Column(length = 100)
    private String paymentPassword;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getPaymentPassword() { return paymentPassword; }
    public void setPaymentPassword(String paymentPassword) { this.paymentPassword = paymentPassword; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
