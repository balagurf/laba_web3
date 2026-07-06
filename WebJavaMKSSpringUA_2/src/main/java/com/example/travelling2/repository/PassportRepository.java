package com.example.travelling2.repository;

import com.example.travelling2.entity.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {

    List<Passport> findByPassportCodeContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String code, String firstName, String lastName
    );

    boolean existsByPassportCodeAndIdNot(String code, Long id);
    boolean existsByPassportCode(String code);
}