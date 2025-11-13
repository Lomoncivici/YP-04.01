package com.mpt.journal.controller;

import com.mpt.journal.entity.Role;
import com.mpt.journal.entity.User;
import com.mpt.journal.service.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('MANAGER')")
public class UserAdminController {

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());
        return "userList";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("userId", null);
        model.addAttribute("username", "");
        model.addAttribute("password", "");
        model.addAttribute("role", Role.USER);
        model.addAttribute("enabled", true);
        model.addAttribute("roles", Role.values());
        return "userForm";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + id));

        model.addAttribute("userId", user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("password", "");
        model.addAttribute("role", user.getRole());
        model.addAttribute("enabled", user.isEnabled());
        model.addAttribute("roles", Role.values());
        return "userForm";
    }

    @PostMapping("/save")
    public String saveUser(
            @RequestParam(required = false) Long userId,
            @RequestParam @NotBlank String username,
            @RequestParam(required = false) String password,
            @RequestParam Role role,
            @RequestParam(defaultValue = "false") boolean enabled
    ) {
        if (userId == null) {
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Пароль обязателен для нового пользователя");
            }
            userService.createUser(username, password, role, enabled);
        } else {
            userService.updateUser(userId, username, password, role, enabled);
        }
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
