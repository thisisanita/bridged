package com.anita.bridged.dto;

import java.time.LocalDateTime;
import java.util.UUID;

// A record is used because this DTO only transfers immutable message data.
// Java automatically generates its constructor, accessors, equals, hashCode,
// and toString, reducing boilerplate compared with a regular class.
public record ChatMessageResponse (
        Long messageId,
        UUID clientMessageId,
        Long chatId,
        Long senderUserId,
        String senderType,
        String messageType,
        String content,
        LocalDateTime createdAt
){
}


