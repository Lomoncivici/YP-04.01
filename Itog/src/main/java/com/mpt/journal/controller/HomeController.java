package com.mpt.journal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String rootRedirect(Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        boolean isManager = hasRole(auth, "ROLE_MANAGER");

        if (isAdmin) {
            return "redirect:/admin/airports";
        } else if (isManager) {
            return "redirect:/manager/users";
        } else {
            return "redirect:/user/home";
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/user/home")
    public String userHome() {
        return "redirect:/user/profile";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "redirect:/admin/airports";
    }
}
