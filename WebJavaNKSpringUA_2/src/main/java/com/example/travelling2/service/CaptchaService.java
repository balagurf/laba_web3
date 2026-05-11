package com.example.travelling2.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    // Хранилище: ID капчи -> изначальный угол поворота
    // Для production-ready проекта лучше использовать Redis с TTL (временем жизни),
    // чтобы старые нерешенные капчи не засоряли память.
    private final Map<String, Integer> captchaStore = new ConcurrentHashMap<>();

    // Вызывается контроллером при открытии формы регистрации
    public CaptchaChallenge generateChallenge() {
        String captchaId = UUID.randomUUID().toString();

        // Генерируем случайный угол от 45 до 315 градусов,
        // чтобы пазл изначально точно не был собран
        int offsetAngle = 45 + (int) (Math.random() * 270);
        captchaStore.put(captchaId, offsetAngle);

        return new CaptchaChallenge(captchaId, offsetAngle);
    }

    // Вызывается при сабмите формы
    public boolean verifyCaptcha(String captchaId, int userRotationAngle) {
        if (captchaId == null || !captchaStore.containsKey(captchaId)) {
            return false; // Капча не найдена или уже использована
        }

        int initialAngle = captchaStore.get(captchaId);
        captchaStore.remove(captchaId); // Одноразовое использование!

        // Проверяем, насколько точно пользователь собрал картинку.
        // Даем погрешность в 15 градусов для удобства.
        int tolerance = 15;

        // Вычисляем итоговое положение (идеально должно быть кратно 360)
        int finalPosition = (initialAngle + userRotationAngle) % 360;

        // Если финальное положение близко к 0 (или 360)
        return finalPosition <= tolerance || finalPosition >= (360 - tolerance);
    }

    // Вспомогательный DTO для передачи на фронтенд
    public record CaptchaChallenge(String captchaId, int initialAngle) {}
}