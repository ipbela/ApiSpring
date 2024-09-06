package com.example.springboot.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VotesRecordDto(@NotNull LocalDate dataRegistro) {

}
