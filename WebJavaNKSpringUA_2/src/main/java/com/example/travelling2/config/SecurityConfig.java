package com.example.travelling2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем статику и страницу регистрации всем
                        .requestMatchers("/css/**", "/js/**", "/register").permitAll()

                        // Только ADMIN имеет доступ к формам создания/редактирования/удаления
                        .requestMatchers("/travellers/create", "/travellers/edit/**", "/travellers/delete/**").hasRole("ADMIN")
                        .requestMatchers("/passports/create", "/passports/edit/**", "/passports/delete/**").hasRole("ADMIN")

                        // Глобально ограничиваем POST, PUT, DELETE запросы только для админа (кроме регистрации)
                        .requestMatchers(HttpMethod.POST, "/travellers/**", "/passports/**").hasRole("ADMIN")

                        // Все остальные запросы (просмотр списков и профилей) доступны всем авторизованным (USER и ADMIN)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
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

    // Возвращаем именно InMemoryUserDetailsManager, чтобы иметь возможность добавлять пользователей в контроллере
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin@khpi.edu.ua")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user@khpi.edu.ua")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}