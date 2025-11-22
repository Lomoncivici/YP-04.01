package com.mpt.journal.repository;

import com.mpt.journal.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends JpaRepository<Route, Long> {
    @Query("""
           select r from Route r
             join r.departureAirport da
             join r.arrivalAirport aa
           where (:search is null or :search = '' 
              or lower(da.city) like lower(concat('%', :search, '%'))
              or lower(aa.city) like lower(concat('%', :search, '%'))
              or lower(da.code) like lower(concat('%', :search, '%'))
              or lower(aa.code) like lower(concat('%', :search, '%')))
           """)
    Page<Route> search(@Param("search") String search, Pageable pageable);
}
