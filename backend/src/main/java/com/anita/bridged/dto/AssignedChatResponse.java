package com.anita.bridged.dto;

import java.time.LocalDateTime;

public class AssignedChatResponse {
    private Long chatId;
    private Long customerId;
    private Long agentId;
    private String agentName;
    private String status;
    private String priority;
    private String topicSkill;
    private String preferredLanguage;
    private LocalDateTime assignedAt;
    private LocalDateTime assignmentDueAt;

    public AssignedChatResponse(Long chatId,
                                Long customerId,
                                Long agentId,
                                String agentName,
                                String status,
                                String priority,
                                String topicSkill,
                                String preferredLanguage,
                                LocalDateTime assignedAt,
                                LocalDateTime assignmentDueAt) {
        this.chatId = chatId;
        this.customerId = customerId;
        this.agentId = agentId;
        this.agentName = agentName;
        this.status = status;
        this.priority = priority;
        this.topicSkill = topicSkill;
        this.preferredLanguage = preferredLanguage;
        this.assignedAt = assignedAt;
        this.assignmentDueAt = assignmentDueAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getTopicSkill() {
        return topicSkill;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getAssignmentDueAt() {
        return assignmentDueAt;
    }
}
