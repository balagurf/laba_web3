package com.example.travelling2.specification;

import com.example.travelling2.entity.Traveller;
import com.example.travelling2.entity.Passport;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class TravellerSpecification {

    public static Specification<Traveller> searchGeneral(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("secondName")), pattern),
                    cb.like(cb.lower(root.get("phone")), pattern)
            );
        };
    }

    public static Specification<Traveller> byPassport(String passportNum) {
        return (root, query, cb) -> {
            if (passportNum == null || passportNum.isBlank()) return null;
            Join<Traveller, Passport> passports = root.join("passports");
            return cb.like(cb.lower(passports.get("passportNumber")), "%" + passportNum.toLowerCase() + "%");
        };
    }

    public static Specification<Traveller> byCountry(String country) {
        return (root, query, cb) -> (country == null || country.isBlank()) ? null : cb.equal(root.get("countryCode"), country);
    }

    public static Specification<Traveller> depositRange(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("deposit"), min, max);
            return min != null ? cb.ge(root.get("deposit"), min) : cb.le(root.get("deposit"), max);
        };
    }
}