package com.example.springboot.repositories;

import com.example.springboot.models.VotesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

public interface VoteRepository extends JpaRepository<VotesModel, UUID> {

        // seleciona o id do restaurante de cada linha da tabela de votos, conta essas linhas e agrupa, depois ordena em ordem decrescente (do maior para o menor)
        @Query("SELECT v.restauranteModelVotes.id, COUNT(v) as voteCount " +
                "FROM VotesModel v " +
                "WHERE v.dataRegistro = :data " +
                "GROUP BY v.restauranteModelVotes.id " +
                "ORDER BY voteCount DESC")
        List<Object[]> findTopVotedRestaurante(@Param("data")LocalDate data); //transforma em uma lista
}
