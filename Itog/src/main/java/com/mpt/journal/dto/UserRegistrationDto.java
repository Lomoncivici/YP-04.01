package com.mpt.journal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, message = "Пароль должен быть не короче 8 символов")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$",
            message = "Пароль должен содержать буквы, цифры и спецсимвол"
    )
    private String password;

    @NotBlank(message = "Подтверждение пароля обязательно")
    private String confirmPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
