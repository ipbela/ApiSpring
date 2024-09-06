package com.example.springboot.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name="restaurante")
public class RestauranteModel implements Serializable {
    private  static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id_restaurante;
    private String nome;
    private String descricao;

    //chave estrangeira informações do restaurante
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_informacoesrestaurante", nullable = false)
    private InfoRestauranteModel infoRestauranteModel;

    public UUID getId_restaurante() {
        return id_restaurante;
    }

    public void setId_restaurante(UUID id_restaurante) {
        this.id_restaurante = id_restaurante;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public InfoRestauranteModel getInfoRestauranteModel() {
        return infoRestauranteModel;
    }

    public void setInfoRestauranteModel(InfoRestauranteModel infoRestauranteModel) {
        this.infoRestauranteModel = infoRestauranteModel;
    }
}
