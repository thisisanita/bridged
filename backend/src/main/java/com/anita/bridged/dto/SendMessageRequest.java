package com.anita.bridged.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// A record is used because this DTO only transfers immutable message data.
// Java automatically generates its constructor, accessors, equals, hashCode,
// and toString, reducing boilerplate compared with a regular class.
public record SendMessageRequest (
    @NotNull UUID clientMessageId,

    @NotBlank
    @Size(max = 4000)
    String content
) {
}