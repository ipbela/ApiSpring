package com.example.springboot.dtos;

import jakarta.validation.constraints.NotNull;

public record Email (@NotNull String nome, @NotNull String email){
}
