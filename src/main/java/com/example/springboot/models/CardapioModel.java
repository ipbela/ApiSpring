package com.example.springboot.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "cardapio")
public class CardapioModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id_cardapio;
    private String prato_principal;
    private String saguao;
    private String sobremesa;
    private String salada;
    private LocalDate data;

    //colocar chave estrangeira id restaurante
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_restaurante", nullable = false)
    private RestauranteModel restauranteModel;

    public UUID getId_cardapio() {
        return id_cardapio;
    }

    public void setId_cardapio(UUID id_cardapio) {
        this.id_cardapio = id_cardapio;
    }

    public String getPrato_principal() {
        return prato_principal;
    }

    public void setPrato_principal(String prato_principal) {
        this.prato_principal = prato_principal;
    }

    public String getSaguao() {
        return saguao;
    }

    public void setSaguao(String saguao) {
        this.saguao = saguao;
    }

    public String getSobremesa() {
        return sobremesa;
    }

    public void setSobremesa(String sobremesa) {
        this.sobremesa = sobremesa;
    }

    public String getSalada() {
        return salada;
    }

    public void setSalada(String salada) {
        this.salada = salada;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public RestauranteModel getRestauranteModel() {
        return restauranteModel;
    }

    public void setRestauranteModel(RestauranteModel restauranteModel) {
        this.restauranteModel = restauranteModel;
    }
}
