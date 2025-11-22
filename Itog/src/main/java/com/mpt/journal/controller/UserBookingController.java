package com.mpt.journal.controller;

import com.mpt.journal.entity.User;
import com.mpt.journal.repository.BookingRepository;
import com.mpt.journal.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/bookings")
public class UserBookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public UserBookingController(BookingRepository bookingRepository,
                                 UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        model.addAttribute("bookings",
                bookingRepository.findByUserOrderByCreatedAtDesc(user));

        return "user/bookings/list";
    }
}