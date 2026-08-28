package com.anita.bridged.controller;

import com.anita.bridged.dto.CompleteTriageRequest;
import com.anita.bridged.dto.CompleteTriageResponse;
import com.anita.bridged.dto.CreateChatResponse;
import com.anita.bridged.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/api/customers/{customerId}/chats")
    public CreateChatResponse createChat(@PathVariable Long customerId) {
        return chatService.createChat(customerId);
    }

    @PatchMapping("/api/chats/{chatId}/triage")
    public CompleteTriageResponse completeTriage (
            @PathVariable Long chatId,
            @RequestBody CompleteTriageRequest request) {
        return chatService.completeTriage(chatId, request);
    }

}
