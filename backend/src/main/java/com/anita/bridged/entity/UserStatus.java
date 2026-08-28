package com.anita.bridged.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_status")
public class UserStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_status_id")
    private Long userStatusId;

    @Column(name = "user_status", nullable = false, unique = true, length = 30)
    private String userStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getUserStatusId() {
        return userStatusId;
    }

    public void setUserStatusId(Long userStatusId) {
        this.userStatusId = userStatusId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
