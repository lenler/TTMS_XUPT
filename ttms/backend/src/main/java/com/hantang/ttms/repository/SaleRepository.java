package com.hantang.ttms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hantang.ttms.domain.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @EntityGraph(attributePaths = {"employee", "customer", "items", "items.ticket", "items.ticket.seat", "items.ticket.schedule"})
    List<Sale> findBySaleTimeBetween(LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"employee", "customer", "items", "items.ticket", "items.ticket.seat", "items.ticket.schedule"})
    @Query("select s from Sale s where s.id = :id")
    Optional<Sale> findWithItemsById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"employee", "customer", "items", "items.ticket", "items.ticket.seat", "items.ticket.schedule"})
    List<Sale> findByEmployeeIdAndSaleTimeBetween(Long employeeId, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"employee", "customer", "items", "items.ticket", "items.ticket.seat", "items.ticket.schedule"})
    List<Sale> findByCustomerId(Long customerId);
}
