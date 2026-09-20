package com.anita.bridged.controller;

import com.anita.bridged.dto.ChatMessageResponse;
import com.anita.bridged.service.ChatMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatMessageRestController {

    private final ChatMessageService chatMessageService;

    public ChatMessageRestController(
            ChatMessageService chatMessageService
    ) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/{chatId}/users/{userId}/messages")
    public List<ChatMessageResponse> getRecentMessages(
            @PathVariable Long chatId,
            @PathVariable Long userId
    ) {
        return chatMessageService.getRecentMessages(chatId, userId);
    }
}
