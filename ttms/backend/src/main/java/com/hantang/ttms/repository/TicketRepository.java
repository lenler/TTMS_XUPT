package com.hantang.ttms.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @EntityGraph(attributePaths = {"seat", "schedule", "schedule.play", "schedule.studio"})
    @Query("select t from Ticket t where t.id = :id")
    Optional<Ticket> findDetailedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"seat", "schedule", "schedule.play", "schedule.studio"})
    List<Ticket> findByScheduleId(Long scheduleId);

    @EntityGraph(attributePaths = {"seat", "schedule", "schedule.play", "schedule.studio"})
    List<Ticket> findByIdIn(Collection<Long> ids);

    long countByScheduleIdAndStatus(Long scheduleId, TicketStatus status);

    long countByScheduleId(Long scheduleId);

    long countByStatus(TicketStatus status);
}
