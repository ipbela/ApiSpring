package com.example.springboot.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "avaliacao_chat")
public class AvaliacaoChatModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id_avaliacao_chat;

    @Enumerated(EnumType.STRING)
    private Avaliacao avaliacao;

    private String data;

    public AvaliacaoChatModel() {
        data = LocalDate.now().toString();
        this.data = data;  // Inicializando a data com a data atual
    }

    // Construtor que utiliza string para inicializar o enum
    public AvaliacaoChatModel(UUID id_avaliacao_chat, String avaliacao) {
        this.id_avaliacao_chat = id_avaliacao_chat;
        this.avaliacao = Avaliacao.fromLabel(avaliacao);
        data = LocalDate.now().toString();
        this.data = data;  // Inicializando a data com a data atual
    }

    public UUID getId_avaliacao_chat() {
        return id_avaliacao_chat;
    }

    public void setId_avaliacao_chat(UUID id_avaliacao_chat) {
        this.id_avaliacao_chat = id_avaliacao_chat;
    }

    public Avaliacao getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Avaliacao avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public enum Avaliacao {
        OTIMO("Ótimo"),
        RUIM("Ruim");

        private final String label;

        Avaliacao(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        // Método para converter uma string para o enum Avaliacao
        public static Avaliacao fromLabel(String label) {
            for (Avaliacao avaliacao : Avaliacao.values()) {
                if (avaliacao.getLabel().equalsIgnoreCase(label)) {
                    return avaliacao;
                }
            }
            throw new IllegalArgumentException("Avaliação inválida: " + label);
        }
    }
}
