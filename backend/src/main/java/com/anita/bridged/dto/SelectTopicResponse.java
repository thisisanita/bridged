package com.anita.bridged.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SelectTopicResponse (
        Long chatId,
        String status,
        String topicSkill,
        String priority,
        String preferredLanguage,
        LocalDateTime triageCompletedAt,
        LocalDateTime assignmentDueAt,
        List<ChatMessageResponse> messages

) {
}
