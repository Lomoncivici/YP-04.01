package com.mpt.journal.controller;

import com.mpt.journal.entity.Role;
import com.mpt.journal.entity.User;
import com.mpt.journal.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/users")
public class ManagerUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerUserController(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "search", required = false) String search,
                       Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("username"));

        Page<User> userPage;
        if (search != null && !search.isBlank()) {
            userPage = userRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("search", search);

        return "managerUsers"; // или "manager/users/list" — как у тебя сейчас
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        User user = new User();
        user.setEnabled(true);

        model.addAttribute("user", user);
        model.addAttribute("roles", allowedRolesForManager());
        model.addAttribute("title", "Создание пользователя");
        model.addAttribute("errorMessage", null);

        return "managerUserForm";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден, id=" + id));

        model.addAttribute("user", existing);
        model.addAttribute("roles", allowedRolesForManager());
        model.addAttribute("title", "Редактирование пользователя");
        model.addAttribute("errorMessage", null);

        return "managerUserForm";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("user") User formUser,
                       BindingResult bindingResult,
                       @RequestParam(name = "rawPassword", required = false) String rawPassword,
                       Model model) {

        boolean creating = (formUser.getId() == null);

        if (formUser.getRole() == Role.ADMIN) {
            bindingResult.rejectValue("role", "role.forbidden", "Менеджер не может назначать роль ADMIN");
        }

        if (creating && userRepository.existsByUsername(formUser.getUsername())) {
            bindingResult.rejectValue("username", "username.exists", "Логин уже используется");
        }

        if (creating) {
            if (rawPassword == null || rawPassword.isBlank()) {
                bindingResult.rejectValue("password", "password.empty", "Пароль обязателен при создании");
            } else if (rawPassword.length() < 8) {
                bindingResult.rejectValue("password", "password.short", "Пароль должен быть не короче 8 символов");
            }
        } else {
            if (rawPassword != null && !rawPassword.isBlank() && rawPassword.length() < 8) {
                bindingResult.rejectValue("password", "password.short", "Пароль должен быть не короче 8 символов");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", allowedRolesForManager());
            model.addAttribute("title", creating ? "Создание пользователя" : "Редактирование пользователя");
            return "managerUserForm";
        }

        User user;

        if (creating) {
            user = new User();
            user.setUsername(formUser.getUsername());
            user.setRole(formUser.getRole());
            user.setEnabled(formUser.isEnabled());
            user.setPassword(passwordEncoder.encode(rawPassword));
        } else {
            user = userRepository.findById(formUser.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден, id=" + formUser.getId()));

            user.setUsername(formUser.getUsername());
            user.setRole(formUser.getRole());
            user.setEnabled(formUser.isEnabled());

            if (rawPassword != null && !rawPassword.isBlank()) {
                user.setPassword(passwordEncoder.encode(rawPassword));
            }
        }

        userRepository.save(user);
        return "redirect:/manager/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/manager/users";
    }

    private List<Role> allowedRolesForManager() {
        return Arrays.stream(Role.values())
                .collect(Collectors.toList());
    }
}