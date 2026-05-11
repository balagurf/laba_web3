package com.example.travelling2.repository;

import com.example.travelling2.entity.Traveller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TravellerRepository extends
        JpaRepository<Traveller, Long>,
        JpaSpecificationExecutor<Traveller> {

    boolean existsByTravelCod(String travelCod);

    boolean existsByTravelCodAndIdNot(String travelCod, Long id);
}