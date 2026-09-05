package com.anita.bridged.dto;

import java.time.LocalDateTime;

public class WaitingChatResponse {
    private Long chatId;
    private Long customerId;
    private String status;
    private String topicSkill;
    private String priority;
    private String preferredLanguage;
    private LocalDateTime triageCompletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime assignmentDueAt;

    public WaitingChatResponse(Long chatId,
                               Long customerId,
                               String status,
                               String topicSkill,
                               String priority,
                               String preferredLanguage,
                               LocalDateTime triageCompletedAt,
                               LocalDateTime createdAt,
                               LocalDateTime assignmentDueAt) {
        this.chatId = chatId;
        this.customerId = customerId;
        this.status = status;
        this.topicSkill = topicSkill;
        this.priority = priority;
        this.preferredLanguage = preferredLanguage;
        this.triageCompletedAt = triageCompletedAt;
        this.createdAt = createdAt;
        this.assignmentDueAt = assignmentDueAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getStatus() {
        return status;
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

    public LocalDateTime getAssignmentDueAt() {
        return assignmentDueAt;
    }
}
