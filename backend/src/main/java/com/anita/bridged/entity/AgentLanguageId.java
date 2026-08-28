package com.anita.bridged.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AgentLanguageId implements Serializable {

    private Long agentId;

    private Long languageId;

    public AgentLanguageId() {
    }

    public AgentLanguageId(Long agentId, Long languageId) {
        this.agentId = agentId;
        this.languageId = languageId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AgentLanguageId that)) {
            return false;
        }

        return Objects.equals(agentId, that.agentId)
                && Objects.equals(languageId, that.languageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId, languageId);
    }
}
