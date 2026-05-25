package com.hantang.ttms.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "schedules")
public class Schedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "studio_id")
    private Studio studio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "play_id")
    private Play play;

    @Column(nullable = false)
    private LocalDateTime showTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Studio getStudio() { return studio; }
    public void setStudio(Studio studio) { this.studio = studio; }
    public Play getPlay() { return play; }
    public void setPlay(Play play) { this.play = play; }
    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
