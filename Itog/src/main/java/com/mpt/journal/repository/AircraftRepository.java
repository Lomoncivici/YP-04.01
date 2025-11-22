package com.mpt.journal.repository;

import com.mpt.journal.entity.Aircraft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    Page<Aircraft> findByModelContainingIgnoreCase(String model, Pageable pageable);
    Page<Aircraft> findAll(Pageable pageable);
}
