package com.mpt.journal.controller;

import com.mpt.journal.entity.User;
import com.mpt.journal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.mpt.journal.repository.BookingRepository;
import java.time.LocalDateTime;
import java.math.BigDecimal;


@Controller
@RequestMapping("/user")
public class UserProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookingRepository bookingRepository;

    public UserProfileController(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));

        model.addAttribute("user", user);

        LocalDateTime now = LocalDateTime.now();

        long totalBookings     = bookingRepository.countByUser(user);
        long upcomingBookings  = bookingRepository.countByUserAndFlight_DepartureTimeAfter(user, now);
        long pastBookings      = bookingRepository.countByUserAndFlight_DepartureTimeBefore(user, now);

        BigDecimal totalSpent  = bookingRepository.sumTotalPriceByUser(user);

        var nextBookingOpt = bookingRepository
                .findFirstByUserAndFlight_DepartureTimeAfterOrderByFlight_DepartureTimeAsc(user, now);

        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("upcomingBookings", upcomingBookings);
        model.addAttribute("pastBookings", pastBookings);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("nextBooking", nextBookingOpt.orElse(null));

        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String fullName,
                                @RequestParam(required = false) String email,
                                Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));

        user.setFullName(fullName);
        user.setEmail(email);

        userRepository.save(user);

        return "redirect:/user/profile?updated";
    }

    @PostMapping("/profile/security")
    public String updateSecurity(@RequestParam String currentPassword,
                                 @RequestParam(required = false) String newUsername,
                                 @RequestParam(required = false) String newPassword,
                                 @RequestParam(required = false) String confirmPassword,
                                 Authentication authentication) {

        String currentUsername = authentication.getName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + currentUsername));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "redirect:/user/profile?pwdError";
        }

        boolean usernameChanged = false;

        if (newUsername != null && !newUsername.isBlank()
                && !newUsername.equals(user.getUsername())) {

            if (userRepository.existsByUsernameIgnoreCaseAndIdNot(newUsername, user.getId())) {
                return "redirect:/user/profile?secError=username";
            }

            user.setUsername(newUsername);
            usernameChanged = true;
        }

        boolean hasNewPwd = newPassword != null && !newPassword.isBlank();
        boolean hasConfirm = confirmPassword != null && !confirmPassword.isBlank();

        if (!hasNewPwd && hasConfirm) {
            return "redirect:/user/profile?secError=pwdEmpty";
        }

        if (hasNewPwd) {
            if (newPassword.length() < 8) {
                return "redirect:/user/profile?secError=pwdShort";
            }

            if (!newPassword.matches("(?=.*[A-Za-z])(?=.*\\d).+")) {
                return "redirect:/user/profile?secError=pwdWeak";
            }

            if (!hasConfirm || !newPassword.equals(confirmPassword)) {
                return "redirect:/user/profile?secError=pwdMismatch";
            }

            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);

        if (usernameChanged) {
            return "redirect:/logout";
        }

        return "redirect:/user/profile?secUpdated";
    }
}