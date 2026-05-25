package com.hantang.ttms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hantang.ttms.domain.Play;

public interface PlayRepository extends JpaRepository<Play, Long> {
    List<Play> findByNameContaining(String name);
}
