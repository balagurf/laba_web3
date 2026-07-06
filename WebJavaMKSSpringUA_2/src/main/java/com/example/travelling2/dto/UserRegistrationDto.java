package com.example.travelling2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введите корректный адрес электронной почты (например, user@example.com)")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

    // Новое поле для тестовой галочки администратора
    private boolean admin;
}