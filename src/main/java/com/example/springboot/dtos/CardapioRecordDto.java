package com.example.springboot.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

//inicializa todos os argumentos que serão salvos na base de dados
public record CardapioRecordDto(@NotNull LocalDate data, @NotBlank String prato_principal, @NotBlank String saguao, @NotBlank String salada, @NotBlank String sobremesa, @NotNull UUID fk_restaurante) {
}
