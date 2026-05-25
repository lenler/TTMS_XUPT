package com.hantang.ttms.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "sale_items")
public class SaleItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id")
    @JsonIgnore
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }
    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
