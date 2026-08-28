package com.anita.bridged.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AgentSkillId implements Serializable {

    private Long agentId;

    private Long skillId;

    public AgentSkillId() {
    }

    public AgentSkillId(Long agentId, Long skillId) {
        this.agentId = agentId;
        this.skillId = skillId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AgentSkillId that)) {
            return false;
        }

        return Objects.equals(agentId, that.agentId)
                && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId, skillId);
    }
}
