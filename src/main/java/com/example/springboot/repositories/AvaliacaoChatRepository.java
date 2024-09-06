package com.example.springboot.repositories;

import com.example.springboot.models.AvaliacaoChatModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvaliacaoChatRepository extends JpaRepository<AvaliacaoChatModel, UUID> {

}
