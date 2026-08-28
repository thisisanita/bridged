package com.anita.bridged.dto;

import java.time.LocalDateTime;

public class WaitingChatResponse {
    private Long chatId;
    private Long customerId;
    private String chatStatus;
    private String topicSkill;
    private String priority;
    private String preferredLanguage;
    private LocalDateTime triageCompletedAt;
    private LocalDateTime createdAt;

    public WaitingChatResponse(Long chatId,
                               Long customerId,
                               String chatStatus,
                               String topicSkill,
                               String priority,
                               String preferredLanguage,
                               LocalDateTime triageCompletedAt,
                               LocalDateTime createdAt) {
        this.chatId = chatId;
        this.customerId = customerId;
        this.chatStatus = chatStatus;
        this.topicSkill = topicSkill;
        this.priority = priority;
        this.preferredLanguage = preferredLanguage;
        this.triageCompletedAt = triageCompletedAt;
        this.createdAt = createdAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getChatStatus() {
        return chatStatus;
    }

    public String getTopicSkill() {
        return topicSkill;
    }

    public String getPriority() {
        return priority;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public LocalDateTime getTriageCompletedAt() {
        return triageCompletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
