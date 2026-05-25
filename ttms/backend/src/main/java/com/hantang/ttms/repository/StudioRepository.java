package com.hantang.ttms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hantang.ttms.domain.Studio;

public interface StudioRepository extends JpaRepository<Studio, Long> {
    List<Studio> findByNameContaining(String name);
}
