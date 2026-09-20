package com.anita.bridged.dto;

public record DemoSessionResponse(
        Long userId,
        String role,
        Long customerId,
        Long agentId,
        String displayName
) {
}