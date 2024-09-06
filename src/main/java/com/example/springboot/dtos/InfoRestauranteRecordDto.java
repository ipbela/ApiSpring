package com.example.springboot.dtos;

import jakarta.validation.constraints.NotNull;

public record InfoRestauranteRecordDto(@NotNull String horario_funcionamento, @NotNull String localizacao, @NotNull String refeicoes_oferecidas) {
}
