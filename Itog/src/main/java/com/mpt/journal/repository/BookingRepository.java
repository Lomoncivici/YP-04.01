package com.mpt.journal.repository;

import com.mpt.journal.entity.Booking;
import com.mpt.journal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    long countByUser(User user);

    long countByUserAndFlight_DepartureTimeAfter(User user, LocalDateTime now);

    long countByUserAndFlight_DepartureTimeBefore(User user, LocalDateTime now);

    @Query("select coalesce(sum(b.totalPrice), 0) from Booking b where b.user = :user")
    BigDecimal sumTotalPriceByUser(@Param("user") User user);

    Optional<Booking> findFirstByUserAndFlight_DepartureTimeAfterOrderByFlight_DepartureTimeAsc(
            User user, LocalDateTime now
    );

    List<Booking> findByUserOrderByCreatedAtDesc(User user);
}