package com.example.springboot.controllers;

import com.example.springboot.models.MessageModel;
import com.example.springboot.services.ChatbotServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    @Autowired
    private ChatbotServices chatbotServices;

    @Operation(description = "Frase inicial do chatbot.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a mensagem inicial."),
            @ApiResponse(responseCode = "400", description = "Não foi possível iniciar o chat, tente novamente mais tarde!")
    })
    @GetMapping("/start")
    public MessageModel startChat() {
        return chatbotServices.startConversation();
    }

    @Operation(description = "De acordo com a informação escolhida pelo usuário, manda uma mensagem correspondente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna as informações para o usuário."),
            @ApiResponse(responseCode = "400", description = "Não foi possível buscar as informações, tente novamente mais tarde!")
    })
    @PostMapping
    public MessageModel chat(@RequestBody MessageModel userMessage) {
        return chatbotServices.handleUserInput(userMessage.getContent());
    }
}
