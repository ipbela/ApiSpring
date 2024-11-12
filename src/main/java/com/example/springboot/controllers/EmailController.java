package com.example.springboot.controllers;

import com.example.springboot.dtos.Email;
import com.example.springboot.models.EmailModel;
import com.example.springboot.repositories.EmailRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmailController {
    @Autowired
    EmailRepository emailRepository;

    @Operation(description = "Registra os emails.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Email registrado com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Não foi possível registrar o email no momento, tente novamente mais tarde!")
    })
    @PostMapping("/register-email")
    public ResponseEntity<Object> saveEmail(@RequestBody @Valid Email email){
        var emailModel = new EmailModel();
        BeanUtils.copyProperties(email, emailModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(emailRepository.save(emailModel));
    }

    @Operation(description = "Retorna os emails encontrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna os emails encontrados."),
            @ApiResponse(responseCode = "400", description = "Não foi possível localizar os emails no momento, tente novamente mais tarde!")
    })
    @GetMapping("/emails")
    public ResponseEntity<Object> getAllEmails(){
        List<EmailModel> emails = emailRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(emails);
    }

}
