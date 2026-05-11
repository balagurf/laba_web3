package com.example.travelling2.service;

import com.example.travelling2.entity.Traveller;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification; // Добавлено
import java.util.List;

public interface TravellerService {
    List<Traveller> findAll();
    List<Traveller> findAll(Sort sort);
    // Новый метод для фильтрации
    List<Traveller> findAll(Specification<Traveller> spec, Sort sort);

    Traveller findById(Long id);
    Traveller save(Traveller traveller);
    void delete(Long id);
    boolean isTravelCodUnique(String travelCod, Long id);
}