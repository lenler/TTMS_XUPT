package com.hantang.ttms.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hantang.ttms.domain.Schedule;
import com.hantang.ttms.domain.Status;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Override
    @EntityGraph(attributePaths = {"studio", "play"})
    List<Schedule> findAll();

    @EntityGraph(attributePaths = {"studio", "play"})
    List<Schedule> findByPlayId(Long playId);

    @EntityGraph(attributePaths = {"studio", "play"})
    List<Schedule> findByStudioIdAndStatus(Long studioId, Status status);

    @EntityGraph(attributePaths = {"studio", "play"})
    List<Schedule> findByStatusAndShowTimeBetween(Status status, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"studio", "play"})
    List<Schedule> findByStatus(Status status);

    boolean existsByStudioIdAndShowTimeBetween(Long studioId, LocalDateTime start, LocalDateTime end);
}
