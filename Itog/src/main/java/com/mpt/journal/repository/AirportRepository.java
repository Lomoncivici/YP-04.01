package com.mpt.journal.repository;

import com.mpt.journal.entity.Airport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    @Query("""
           select a from Airport a
           where (:search is null or :search = '' 
              or lower(a.name)  like lower(concat('%', :search, '%'))
              or lower(a.city)  like lower(concat('%', :search, '%'))
              or lower(a.code)  like lower(concat('%', :search, '%')))
           """)
    Page<Airport> search(@Param("search") String search, Pageable pageable);
}