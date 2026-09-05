package com.anita.bridged.dto;

import java.time.LocalDateTime;

public class ChatDetailsResponse {

    private Long chatId;
    private Long customerId;
    private String customerName;
    private Long assignedAgentId;
    private String assignedAgentName;
    private String status;
    private String topicSkill;
    private String priority;
    private String preferredLanguage;
    private LocalDateTime createdAt;
    private LocalDateTime triageCompletedAt;
    private LocalDateTime assignmentDueAt;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime closedAt;

    public ChatDetailsResponse(Long chatId, Long customerId, String customerName, Long assignedAgentId, String assignedAgentName, String status, String topicSkill, String priority, String preferredLanguage, LocalDateTime createdAt, LocalDateTime triageCompletedAt, LocalDateTime assignmentDueAt, LocalDateTime assignedAt, LocalDateTime acceptedAt, LocalDateTime closedAt) {
        this.chatId = chatId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.assignedAgentId = assignedAgentId;
        this.assignedAgentName = assignedAgentName;
        this.status = status;
        this.topicSkill = topicSkill;
        this.priority = priority;
        this.preferredLanguage = preferredLanguage;
        this.createdAt = createdAt;
        this.triageCompletedAt = triageCompletedAt;
        this.assignmentDueAt = assignmentDueAt;
        this.assignedAt = assignedAt;
        this.acceptedAt = acceptedAt;
        this.closedAt = closedAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getAssignedAgentId() {
        return assignedAgentId;
    }

    public String getAssignedAgentName() {
        return assignedAgentName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getTriageCompletedAt() {
        return triageCompletedAt;
    }

    public LocalDateTime getAssignmentDueAt() {
        return assignmentDueAt;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }
}


