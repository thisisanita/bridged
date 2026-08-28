package com.anita.bridged.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "priority")
public class Priority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "priority_id")
    private Long priorityId;

    @Column(name = "priority_level", nullable = false, unique = true)
    private Integer priorityLevel;

    @Column(name = "priority", nullable = false, unique = true, length = 30)
    private String priority;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
