package com.example.travelling2.service;

import com.example.travelling2.entity.Traveller;
import com.example.travelling2.repository.TravellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TravellerServiceImpl implements TravellerService {

    private final TravellerRepository repo;

    @Override
    public List<Traveller> findAll() { return repo.findAll(); }

    @Override
    public List<Traveller> findAll(Sort sort) { return repo.findAll(sort); }

    @Override
    public Traveller findById(Long id) { return repo.findById(id).orElseThrow(); }

    @Override
    public Traveller save(Traveller traveller) { return repo.save(traveller); }

    @Override
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public boolean isTravelCodUnique(String travelCod, Long id) {
        if (id == null) return !repo.existsByTravelCod(travelCod);
        return !repo.existsByTravelCodAndIdNot(travelCod, id);
    }
}