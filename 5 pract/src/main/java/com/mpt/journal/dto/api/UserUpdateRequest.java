package com.mpt.journal.dto.api;

import com.mpt.journal.entity.Role;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @Size(min = 8, max = 100)
    private String password;

    private Role role;

    private Boolean enabled;

    public UserUpdateRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
