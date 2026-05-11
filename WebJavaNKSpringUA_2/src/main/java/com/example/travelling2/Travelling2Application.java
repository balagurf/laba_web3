package com.example.travelling2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Добавляем exclude
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Travelling2Application {

    public static void main(String[] args) {
        SpringApplication.run(Travelling2Application.class, args);
    }
}