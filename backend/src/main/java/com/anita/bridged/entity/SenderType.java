package com.anita.bridged.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sender_type")
public class SenderType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sender_type_id")
    private Long senderTypeId;

    @Column(name = "sender", nullable = false, unique = true, length = 30)
    private String sender;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getSenderTypeId() {
        return senderTypeId;
    }

    public void setSenderTypeId(Long senderTypeId) {
        this.senderTypeId = senderTypeId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
