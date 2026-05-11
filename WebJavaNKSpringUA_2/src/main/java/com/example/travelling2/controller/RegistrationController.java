package com.example.travelling2.controller;

import com.example.travelling2.dto.UserRegistrationDto;
import com.example.travelling2.service.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());

        // Генерируем новую капчу для формы
        CaptchaService.CaptchaChallenge challenge = captchaService.generateChallenge();
        model.addAttribute("captchaId", challenge.captchaId());
        model.addAttribute("initialAngle", challenge.initialAngle());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
                               BindingResult result,
                               @RequestParam("captchaId") String captchaId,
                               @RequestParam("rotationAngle") int rotationAngle,
                               Model model) {

        // 1. Проверка нашей новой капчи (крутящихся дисков)
        if (!captchaService.verifyCaptcha(captchaId, rotationAngle)) {
            model.addAttribute("captchaError", "Пазл собран неверно. Попробуйте еще раз!");

            // Если капча не прошла, нужно сгенерировать новую для следующей попытки
            CaptchaService.CaptchaChallenge newChallenge = captchaService.generateChallenge();
            model.addAttribute("captchaId", newChallenge.captchaId());
            model.addAttribute("initialAngle", newChallenge.initialAngle());

            return "register";
        }

        // 2. Проверка валидации полей (email, пароль)
        if (result.hasErrors()) {
            // Пересоздаем капчу при ошибках валидации, чтобы не "протухла"
            CaptchaService.CaptchaChallenge newChallenge = captchaService.generateChallenge();
            model.addAttribute("captchaId", newChallenge.captchaId());
            model.addAttribute("initialAngle", newChallenge.initialAngle());
            return "register";
        }

        // 3. Проверка, существует ли уже такой пользователь
        if (userDetailsManager.userExists(userDto.getEmail())) {
            result.rejectValue("email", "duplicate", "Пользователь с таким email уже существует");

            CaptchaService.CaptchaChallenge newChallenge = captchaService.generateChallenge();
            model.addAttribute("captchaId", newChallenge.captchaId());
            model.addAttribute("initialAngle", newChallenge.initialAngle());
            return "register";
        }

        // 4. Создание и сохранение нового пользователя
        userDetailsManager.createUser(User.builder()
                .username(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles("USER")
                .build());

        return "redirect:/login?registered";
    }
}