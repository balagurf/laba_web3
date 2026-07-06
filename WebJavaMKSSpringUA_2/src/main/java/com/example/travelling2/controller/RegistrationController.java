package com.example.travelling2.controller;

import com.example.travelling2.dto.UserRegistrationDto;
import com.example.travelling2.entity.User;
import com.example.travelling2.repository.UserRepository;
import com.example.travelling2.service.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final UserRepository userRepository; // Замена InMemoryUserDetailsManager
    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; //
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());

        // Генерация капчи
        CaptchaService.CaptchaChallenge challenge = captchaService.generateChallenge();
        model.addAttribute("captchaId", challenge.captchaId());
        model.addAttribute("initialAngle", challenge.initialAngle());

        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
                                      BindingResult result,
                                      @RequestParam("captchaId") String captchaId,
                                      @RequestParam("rotationAngle") int userRotation, // Берем из скрытого поля HTML
                                      Model model) {

        // 1. Проверка капчи
        if (!captchaService.verifyCaptcha(captchaId, userRotation)) {
            result.rejectValue("email", "captcha", "Капча решена неверно. Попробуйте снова.");
            refreshCaptcha(model);
            return "register";
        }

        // 2. Проверка ошибок валидации (длина пароля и т.д.)
        if (result.hasErrors()) {
            refreshCaptcha(model);
            return "register";
        }

        // 3. Проверка существования пользователя в базе
        if (userRepository.existsByEmail(userDto.getEmail())) {
            result.rejectValue("email", "duplicate", "Пользователь с таким email уже существует");
            refreshCaptcha(model);
            return "register";
        }

        // 4. Определение роли по галочке и сохранение в БД
        String role = userDto.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";

        User newUser = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword())) // Хешируем пароль
                .role(role)
                .build();

        userRepository.save(newUser); // Записываем в таблицу users

        return "redirect:/login?registered";
    }

    private void refreshCaptcha(Model model) {
        CaptchaService.CaptchaChallenge newChallenge = captchaService.generateChallenge();
        model.addAttribute("captchaId", newChallenge.captchaId());
        model.addAttribute("initialAngle", newChallenge.initialAngle());
    }
}