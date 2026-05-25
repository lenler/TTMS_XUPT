package com.hantang.ttms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hantang.ttms.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @EntityGraph(attributePaths = {"studio"})
    List<Seat> findByStudioIdOrderByRowNoAscColNoAsc(Long studioId);
    long countByStudioId(Long studioId);
}
