package com.example.travelling2.controller;

import com.example.travelling2.entity.User;
import com.example.travelling2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    // Подключаем сервис
    private final UserService userService;

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        // Защита: если пользователь не авторизован
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        model.addAttribute("username", username);

        // Достаем пользователя из БД, чтобы передать его реальную роль
        User user = userService.findByEmail(username);

        // Оборачиваем роль в SimpleGrantedAuthority, чтобы Thymeleaf смог прочитать свойство 'authority'
        model.addAttribute("roles", Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));

        return "profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        if (principal == null) return "redirect:/login";

        // Валидация
        if (newPassword == null || newPassword.trim().length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пароль должен содержать минимум 6 символов.");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Введенные пароли не совпадают.");
            return "redirect:/profile";
        }

        try {
            String email = principal.getName();

            // Вызываем логику обновления пароля в сервисе
            userService.updatePassword(email, newPassword);

            redirectAttributes.addFlashAttribute("successMessage", "Ваш пароль был успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при обновлении пароля.");
        }

        // Паттерн Post-Redirect-Get для избежания повторной отправки формы
        return "redirect:/profile";
    }
}