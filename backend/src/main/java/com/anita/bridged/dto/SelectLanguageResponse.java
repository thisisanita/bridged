package com.anita.bridged.dto;

import java.util.List;

public record SelectLanguageResponse (
        Long chatId,
        String status,
        String preferredLanguage,
        List<ChatMessageResponse> messages
) {
}
