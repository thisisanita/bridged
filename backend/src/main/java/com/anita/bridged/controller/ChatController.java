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

    @PatchMapping("/chats/{chatId}/triage/language")
    public SelectLanguageResponse selectLanguage(
            @PathVariable("chatId") Long chatId,
            @RequestBody SelectLanguageRequest request
    ) {
        return chatService.selectLanguage(chatId, request);
    }

    @PatchMapping("/chats/{chatId}/triage/topic")
    public SelectTopicResponse selectTopic(
        @PathVariable("chatId") Long chatId,
        @RequestBody SelectTopicRequest request
        ) {
            return chatService.selectTopic(chatId, request);

        }

}
