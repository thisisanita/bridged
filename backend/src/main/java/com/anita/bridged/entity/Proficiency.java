package com.anita.bridged.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proficiency")
public class Proficiency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proficiency_id")
    private Long proficiencyId;

    @Column(name = "proficiency_level", nullable = false, unique = true)
    private Integer proficiencyLevel;

    @Column(name = "proficiency", nullable = false, unique = true, length = 30)
    private String proficiency;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getProficiencyId() {
        return proficiencyId;
    }

    public void setProficiencyId(Long proficiencyId) {
        this.proficiencyId = proficiencyId;
    }

    public Integer getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(Integer proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
