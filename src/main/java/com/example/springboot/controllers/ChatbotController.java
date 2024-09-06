package com.example.springboot.controllers;

import com.example.springboot.models.MessageModel;
import com.example.springboot.services.ChatbotServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    @Autowired
    private ChatbotServices chatbotServices;

    @GetMapping("/start")
    public MessageModel startChat() {
        return chatbotServices.startConversation();
    }

    @PostMapping
    public MessageModel chat(@RequestBody MessageModel userMessage) {
        return chatbotServices.handleUserInput(userMessage.getContent());
    }
}
