package com.anita.bridged.dto;

import java.time.LocalDateTime;

public class AcceptChatResponse {

    private Long chatId;
    private Long agentId;
    private String status;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;

    public AcceptChatResponse(Long chatId, Long agentId, String status, LocalDateTime assignedAt, LocalDateTime acceptedAt) {
        this.chatId = chatId;
        this.agentId = agentId;
        this.status = status;
        this.assignedAt = assignedAt;
        this.acceptedAt = acceptedAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }
}
