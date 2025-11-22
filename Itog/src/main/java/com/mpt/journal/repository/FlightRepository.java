package com.mpt.journal.repository;

import com.mpt.journal.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("""
           select f from Flight f
             join f.route r
             join r.departureAirport da
             join r.arrivalAirport aa
           where (:search is null or :search = ''
              or lower(f.flightNumber) like lower(concat('%', :search, '%'))
              or lower(da.city) like lower(concat('%', :search, '%'))
              or lower(aa.city) like lower(concat('%', :search, '%')))
           """)
    Page<Flight> search(@Param("search") String search, Pageable pageable);
}
