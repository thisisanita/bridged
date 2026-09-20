package com.anita.bridged.exception;

public class ChatAccessDeniedException extends RuntimeException {

    public ChatAccessDeniedException (Long chatId, Long userId) {
        super(
                "User " + userId
                        + " is not a participant in chat " + chatId
        );
    }
}
