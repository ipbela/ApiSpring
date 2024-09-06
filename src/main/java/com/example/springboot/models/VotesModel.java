package com.example.springboot.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "votes")
public class VotesModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id_votes;

    private LocalDate dataRegistro;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_idrestaurantevotes", nullable = false)
    private RestauranteModel restauranteModelVotes;

    public UUID getId_votes() {
        return id_votes;
    }

    public void setId_votes(UUID id_votes) {
        this.id_votes = id_votes;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public RestauranteModel getRestauranteModelId() {
        return restauranteModelVotes;
    }

    public void setRestauranteModelId(RestauranteModel restauranteModelId) {
        this.restauranteModelVotes = restauranteModelId;
    }
}
