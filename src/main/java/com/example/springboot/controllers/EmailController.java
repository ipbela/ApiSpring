package com.example.springboot.controllers;

import com.example.springboot.dtos.Email;
import com.example.springboot.models.EmailModel;
import com.example.springboot.repositories.EmailRepository;
import com.example.springboot.services.EmailService;
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
    private EmailService emailService;

    @Autowired
    EmailRepository emailRepository;

    @GetMapping("/send-email")
    public String sendEmail() {
        emailService.sendEmailsToAllActive();
        return "Processo de envio de e-mail concluído.";
    }

    //método para registrar emails
    @PostMapping("/register-email")
    public ResponseEntity<Object> saveEmail(@RequestBody @Valid Email email){
        var emailModel = new EmailModel();
        BeanUtils.copyProperties(email, emailModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(emailRepository.save(emailModel));
    }

    //método para pegar os emails
    @GetMapping("/emails")
    public ResponseEntity<Object> getAllEmails(){
        List<EmailModel> emails = emailRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(emails);
    }

}
