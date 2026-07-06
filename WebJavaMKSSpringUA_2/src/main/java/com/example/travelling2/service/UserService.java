package com.example.travelling2.service;

import com.example.travelling2.dto.UserRegistrationDto;
import com.example.travelling2.entity.User;
import com.example.travelling2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void registerNewUserAccount(UserRegistrationDto userDto) {
        String role = userDto.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";

        User user = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);
    }

    // --- НОВЫЕ МЕТОДЫ ДЛЯ ПРОФИЛЯ ---

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public void updatePassword(String email, String rawNewPassword) {
        // Находим пользователя
        User user = findByEmail(email);

        // Хешируем новый пароль и устанавливаем его
        user.setPassword(passwordEncoder.encode(rawNewPassword));

        // Сохраняем обновленного пользователя в базу данных
        userRepository.save(user);
    }
}