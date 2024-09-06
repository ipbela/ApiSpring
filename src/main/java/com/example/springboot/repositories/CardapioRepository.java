package com.example.springboot.repositories;

import com.example.springboot.models.CardapioModel;
import com.example.springboot.models.RestauranteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardapioRepository extends JpaRepository<CardapioModel, UUID> {
    List<CardapioModel> findByRestauranteModel(RestauranteModel restauranteModel);
}
