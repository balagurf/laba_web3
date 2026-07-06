package com.example.travelling2.service;

import com.example.travelling2.entity.Passport;
import com.example.travelling2.entity.Traveller;
import com.example.travelling2.repository.PassportRepository;
import com.example.travelling2.repository.TravellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassportService {

    private final PassportRepository repo;
    private final TravellerRepository travellerRepository;

    public List<Passport> findAll() {
        return repo.findAll();
    }

    public Passport findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Passport not found"));
    }

    public Passport save(Passport passport) {
        // Подставляем существующего Traveller из БД (или null)
        if (passport.getTraveller() != null && passport.getTraveller().getId() != null) {
            Traveller traveller = travellerRepository.findById(passport.getTraveller().getId())
                    .orElse(null);
            passport.setTraveller(traveller);
        } else {
            passport.setTraveller(null);
        }

        // Защита от null balance — гарантируем ненулевое значение
        if (passport.getBalance() == null) {
            passport.setBalance(BigDecimal.ZERO);
        }

        // Защита от отрицательного childrenCount
        if (passport.getChildrenCount() < 0) {
            passport.setChildrenCount(0);
        }

        return repo.save(passport);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public boolean isPassportCodeUnique(String code, Long id) {
        return id == null ? !repo.existsByPassportCode(code) : !repo.existsByPassportCodeAndIdNot(code, id);
    }

    public String generatePassportNumber() {
        int number = (int) (Math.random() * 9_000_000) + 1_000_000;
        return "AB" + number;
    }

    public String generatePassportCode() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 12);
    }

    public List<Passport> search(String search) {
        if (search == null || search.isBlank()) return repo.findAll();
        return repo.findByPassportCodeContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                search, search, search
        );
    }
}
