package com.anita.bridged.exception;

public class AgentNotFoundException extends RuntimeException {

    public AgentNotFoundException(Long agentId) {
        super("Agent not found with id: " + agentId);
    }


}
