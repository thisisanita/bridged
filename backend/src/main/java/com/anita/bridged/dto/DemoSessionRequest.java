package com.anita.bridged.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DemoSessionRequest(
        @NotBlank String role,
        @NotNull Long userId
) {
}