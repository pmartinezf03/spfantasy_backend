package com.spfantasy.backend.controller;

import com.spfantasy.backend.service.ChatBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/{username}")
    public ResponseEntity<String> procesarPregunta(@PathVariable String username, @RequestBody String pregunta) {
        try {
            String respuesta = chatBotService.procesarPregunta(username, pregunta);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }
}
