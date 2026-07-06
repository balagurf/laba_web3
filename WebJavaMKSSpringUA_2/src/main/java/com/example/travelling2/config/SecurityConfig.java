package com.example.travelling2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ к статике, входу и регистрации для всех
                        .requestMatchers("/css/**", "/js/**", "/register", "/login").permitAll()
                        // Ограничиваем права администратора на изменение данных
                        .requestMatchers("/travellers/create", "/travellers/edit/**", "/travellers/delete/**").hasRole("ADMIN")
                        .requestMatchers("/passports/create", "/passports/edit/**", "/passports/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/travellers/**", "/passports/**").hasRole("ADMIN")
                        // Все остальные страницы требуют входа в систему
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Используем кастомную страницу входа
                        .defaultSuccessUrl("/travellers", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Используем BCrypt для надежного хеширования паролей в базе
        return new BCryptPasswordEncoder();
    }
}