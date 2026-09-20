package com.anita.bridged.repository;

import com.anita.bridged.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatMessage>
    findByChatSession_ChatIdAndSenderUser_UserIdAndClientMessageId(
            Long chatId,
            Long senderUserId,
            UUID clientMessageId
    );

    List<ChatMessage>
    findTop100ByChatSession_ChatIdOrderByMessageIdDesc(Long chatId);
}
