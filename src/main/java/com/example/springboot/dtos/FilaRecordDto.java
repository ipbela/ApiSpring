package com.example.springboot.dtos;

import jakarta.validation.constraints.NotNull;

public record FilaRecordDto(@NotNull boolean isAtivado, @NotNull boolean localizacao) {
}
