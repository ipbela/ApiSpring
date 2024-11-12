package com.example.springboot.controllers;

import com.example.springboot.models.RestauranteModel;
import com.example.springboot.repositories.RestauranteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestauranteController {

    @Autowired
    RestauranteRepository restauranteRepository;

    @Operation(description = "Busca os restaurantes registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna os restaurantes encontrados."),
            @ApiResponse(responseCode = "400", description = "Não foi possível localizar os restaurantes, tente novamente mais tarde!")
    })
    @GetMapping("/restaurantes")
    public ResponseEntity<List<RestauranteModel>> getAllRestaurants() {
        List<RestauranteModel> restaurantes = restauranteRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(restaurantes);
    }
}
