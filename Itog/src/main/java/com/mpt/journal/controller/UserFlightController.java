package com.mpt.journal.controller;

import com.mpt.journal.entity.Booking;
import com.mpt.journal.entity.Flight;
import com.mpt.journal.entity.User;
import com.mpt.journal.repository.BookingRepository;
import com.mpt.journal.repository.FlightRepository;
import com.mpt.journal.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/user/flights")
public class UserFlightController {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public UserFlightController(FlightRepository flightRepository,
                                BookingRepository bookingRepository,
                                UserRepository userRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("flights", flightRepository.findAll());
        return "user/flights/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рейс не найден, id=" + id));
        model.addAttribute("flight", flight);
        return "user/flights/details";
    }

    @PostMapping("/{id}/book")
    public String book(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails userDetails) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рейс не найден, id=" + id));

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setStatus("BOOKED");
        booking.setTotalPrice(
                flight.getBasePrice() != null ? flight.getBasePrice() : BigDecimal.ZERO
        );

        bookingRepository.save(booking);

        return "redirect:/user/bookings";
    }
}