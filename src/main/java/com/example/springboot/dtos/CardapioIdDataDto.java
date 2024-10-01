package com.example.springboot.dtos;

import java.time.LocalDate;
import java.util.UUID;

public class CardapioIdDataDto {
    private UUID id;
    private String data;

    public CardapioIdDataDto(UUID id, String data) {
        this.id = id;
        this.data = data;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

