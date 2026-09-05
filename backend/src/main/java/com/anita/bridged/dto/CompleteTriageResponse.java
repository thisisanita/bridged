package com.anita.bridged.dto;

import java.time.LocalDateTime;

public class CompleteTriageResponse {

    private Long chatId;
    private String status;
    private String topicSkill;
    private String priority;
    private String preferredLanguage;
    private LocalDateTime triageCompletedAt;
    private LocalDateTime assignmentDueAt;

    public CompleteTriageResponse(Long chatId,
                                  String status,
                                  String topicSkill,
                                  String priority,
                                  String preferredLanguage,
                                  LocalDateTime triageCompletedAt,
                                  LocalDateTime assignmentDueAt) {
        this.chatId = chatId;
        this.status = status;
        this.topicSkill = topicSkill;
        this.priority = priority;
        this.preferredLanguage = preferredLanguage;
        this.triageCompletedAt = triageCompletedAt;
        this.assignmentDueAt = assignmentDueAt;
    }

    public Long getChatId() {
        return chatId;
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

    public LocalDateTime getAssignmentDueAt() {
        return assignmentDueAt;
    }
}
