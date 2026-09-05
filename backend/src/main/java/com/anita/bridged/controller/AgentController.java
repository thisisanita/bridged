package com.anita.bridged.controller;

import com.anita.bridged.dto.ChatDetailsResponse;
import com.anita.bridged.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final ChatService chatService;

    public AgentController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{agentId}/chats/open")
    public List<ChatDetailsResponse> listOpenChatsForAgent(
            @PathVariable("agentId") Long agentId) {
        return chatService.listOpenChatsForAgent(agentId);
    }
}
