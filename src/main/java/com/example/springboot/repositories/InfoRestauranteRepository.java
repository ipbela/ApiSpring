package com.example.springboot.repositories;

import com.example.springboot.models.InfoRestauranteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InfoRestauranteRepository extends JpaRepository<InfoRestauranteModel, UUID> {

    @Query("SELECT i FROM InfoRestauranteModel i")
    InfoRestauranteModel findInfos();

}
