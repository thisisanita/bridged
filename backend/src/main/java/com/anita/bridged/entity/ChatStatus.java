package com.anita.bridged.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_status")
public class ChatStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_status_id")
    private Long chatStatusId;

    @Column(name = "status", nullable = false, unique = true, length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getChatStatusId() {
        return chatStatusId;
    }

    public void setChatStatusId(Long chatStatusId) {
        this.chatStatusId = chatStatusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
