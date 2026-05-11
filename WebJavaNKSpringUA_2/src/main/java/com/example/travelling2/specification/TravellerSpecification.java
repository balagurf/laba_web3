package com.example.travelling2.specification;

import com.example.travelling2.entity.Traveller;
import org.springframework.data.jpa.domain.Specification;

public class TravellerSpecification {

    public static Specification<Traveller> searchName(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("secondName")), pattern)
            );
        };
    }

    public static Specification<Traveller> minDeposit(Double min) {
        return (root, query, cb) -> min == null ? null : cb.ge(root.get("deposit"), min);
    }

    public static Specification<Traveller> maxDeposit(Double max) {
        return (root, query, cb) -> max == null ? null : cb.le(root.get("deposit"), max);
    }
}