package com.anita.bridged.controller;

import com.anita.bridged.dto.*;
import com.anita.bridged.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/customers/{customerId}/chats")
    public CreateChatResponse createChat(@PathVariable("customerId") Long customerId) {
        return chatService.createChat(customerId);
    }

    @PatchMapping("/chats/{chatId}/triage")
    public CompleteTriageResponse completeTriage (
            @PathVariable("chatId") Long chatId,
            @RequestBody CompleteTriageRequest request) {
        return chatService.completeTriage(chatId, request);
    }

    @GetMapping("/chats/waiting")
    public List<WaitingChatResponse> listWaitingChats() {
        return chatService.listWaitingChats();
    }

    @PatchMapping("/chats/{chatId}/accept")
    public AcceptChatResponse acceptChat(
            @PathVariable("chatId") Long chatId) {
        return chatService.acceptChat(chatId);
    }

    @PatchMapping("/chats/{chatId}/close")
    public CloseChatResponse closeChat(
            @PathVariable("chatId") Long chatId) {
        return chatService.closeChat(chatId);
    }

    @GetMapping("/chats/{chatId}")
    public ChatDetailsResponse getChatDetails(
            @PathVariable("chatId") Long chatId) {
        return chatService.getChatDetails(chatId);
    }

}
