package com.example.travelling2.repository;

import com.example.travelling2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по email.
     * @param email почта пользователя.
     * @return Optional с найденным пользователем.
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверка существования пользователя с данным email.
     * @param email почта для проверки.
     * @return true, если пользователь существует.
     */
    boolean existsByEmail(String email);
}