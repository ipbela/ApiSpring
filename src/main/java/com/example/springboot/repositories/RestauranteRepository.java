package com.example.springboot.repositories;

import com.example.springboot.models.RestauranteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestauranteRepository extends JpaRepository<RestauranteModel, UUID> {
    RestauranteModel findByNome(String nome);
}
