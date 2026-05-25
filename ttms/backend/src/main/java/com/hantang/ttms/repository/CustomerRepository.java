package com.hantang.ttms.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hantang.ttms.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
    List<Customer> findByUsernameContainingOrNameContaining(String username, String name);
    boolean existsByUsername(String username);
}
