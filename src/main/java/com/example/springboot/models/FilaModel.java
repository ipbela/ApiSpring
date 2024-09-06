package com.example.springboot.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "fila")
public class FilaModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id_fila;

    private boolean isAtivado;
    private String localizacao;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_idrestaurante", nullable = false)
    private RestauranteModel restauranteIdModel;

    public UUID getId() {
        return id_fila;
    }

    public void setId(UUID id_fila) {
        this.id_fila = id_fila;
    }

    public boolean isAtivado() {
        return isAtivado;
    }

    public void setAtivado(boolean ativado) {
        isAtivado = ativado;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public RestauranteModel getRestauranteIdModel() {
        return restauranteIdModel;
    }

    public void setRestauranteIdModel(RestauranteModel restauranteIdModel) {
        this.restauranteIdModel = restauranteIdModel;
    }
}
