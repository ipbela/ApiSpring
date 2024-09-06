package com.example.springboot.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.format.DateTimeFormatter;

public record CardapioRecordDto(@NotNull DateTimeFormatter data, @NotNull String prato_principal, @NotNull String saguao, @NotNull String salada) {
}
