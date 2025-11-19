package com.mpt.journal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostLoginController {

    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {

        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        boolean isManager = hasRole(authentication, "ROLE_MANAGER");
        boolean isUser = hasRole(authentication, "ROLE_USER");

        if (isAdmin) {
            return "redirect:/students";
        } else if (isManager) {
            return "redirect:/users";
        } else if (isUser) {
            return "redirect:/students";
        } else {
            return "redirect:/login?errorRole";
        }
    }

    private boolean hasRole(Authentication auth, String role) {
        if (auth == null) return false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (role.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
