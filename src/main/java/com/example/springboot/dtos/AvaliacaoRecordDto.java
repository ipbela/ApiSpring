package com.example.springboot.dtos;

import com.example.springboot.models.AvaliacaoChatModel;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AvaliacaoRecordDto(@NotNull AvaliacaoChatModel.Avaliacao Avaliacao, @NotNull LocalDate data) {
}
