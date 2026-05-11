package com.example.travelling2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    // TODO: Раскомментируй и используй свои реальные сервисы
    // private final UserService userService;
    // private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        // Защита: если пользователь не авторизован (хотя Spring Security должен это отловить)
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        model.addAttribute("username", username);

        // TODO: Достань пользователя из БД, чтобы передать его роли
        // User user = userService.findByUsername(username);
        // model.addAttribute("roles", user.getRoles());

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
            // TODO: Раскомментируй логику обновления пароля в базе
            // String username = principal.getName();
            // String encodedPassword = passwordEncoder.encode(newPassword);
            // userService.updatePassword(username, encodedPassword);

            redirectAttributes.addFlashAttribute("successMessage", "Ваш пароль был успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при обновлении пароля.");
        }

        // Паттерн Post-Redirect-Get для избежания повторной отправки формы
        return "redirect:/profile";
    }
}