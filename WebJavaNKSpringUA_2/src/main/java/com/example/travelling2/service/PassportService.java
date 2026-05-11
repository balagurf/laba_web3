package com.example.travelling2.service;

import com.example.travelling2.entity.Passport;
import com.example.travelling2.repository.PassportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassportService {

    private final PassportRepository repo;

    public List<Passport> findAll() { return repo.findAll(); }

    public List<Passport> search(String search) {
        if (search == null || search.isBlank()) return repo.findAll();
        return repo.findByPassportCodeContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                search, search, search);
    }

    public Passport findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Passport not found"));
    }

    public Passport save(Passport passport) {
        return repo.save(passport);
    }

    public void delete(Long id) { repo.deleteById(id); }

    public boolean isPassportCodeUnique(String code, Long id) {
        if (id == null) return !repo.existsByPassportCode(code);
        return !repo.existsByPassportCodeAndIdNot(code, id);
    }

    public String generatePassportNumber() {
        return "AB" + ((int) (Math.random() * 9000000) + 1000000);
    }

    public String generatePassportCode() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 12);
    }
}