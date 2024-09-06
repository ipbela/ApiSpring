package com.example.springboot.controllers;

import com.example.springboot.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    //método para pegar os emails
    @GetMapping("/send-email")
    public String sendEmail() {
        emailService.sendEmailsToAllActive();
        return "Processo de envio de e-mail concluído.";
    }
}
