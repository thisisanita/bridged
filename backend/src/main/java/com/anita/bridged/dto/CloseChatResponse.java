package com.anita.bridged.dto;

import java.time.LocalDateTime;

public class CloseChatResponse {

    private Long chatId;
    private Long agentId;
    private String status;
    private LocalDateTime closedAt;
    private Integer agentOpenChatCount;

    public CloseChatResponse(Long chatId, Long agentId, String status, LocalDateTime closedAt, Integer agentOpenChatCount) {
        this.chatId = chatId;
        this.agentId = agentId;
        this.status = status;
        this.closedAt = closedAt;
        this.agentOpenChatCount = agentOpenChatCount;
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

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public Integer getAgentOpenChatCount() {
        return agentOpenChatCount;
    }
}
