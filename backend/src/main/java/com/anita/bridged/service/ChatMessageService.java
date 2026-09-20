package com.anita.bridged.service;

import com.anita.bridged.dto.ChatMessageResponse;
import com.anita.bridged.dto.SendMessageRequest;
import com.anita.bridged.entity.*;
import com.anita.bridged.exception.ChatAccessDeniedException;
import com.anita.bridged.exception.ChatNotFoundException;
import com.anita.bridged.exception.InvalidChatStateException;
import com.anita.bridged.exception.ReferenceDataNotFoundException;
import com.anita.bridged.repository.ChatMessageRepository;
import com.anita.bridged.repository.ChatSessionRepository;
import com.anita.bridged.repository.SenderTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final SenderTypeRepository senderTypeRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatSessionRepository chatSessionRepository, SenderTypeRepository senderTypeRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.senderTypeRepository = senderTypeRepository;
    }

    @Transactional
    public ChatMessageResponse saveMessage(
            Long chatId,
            Long senderUserId,
            SendMessageRequest request
    ) {
        ChatSession chat = chatSessionRepository
                .findByIdForUpdate(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        String status = chat.getChatStatus().getStatus();
        if (!"ACTIVE".equals(status)) {
            throw new InvalidChatStateException(
                    "Chat " + chatId
                            + " cannot receive messages because it is currently "
                            + status
            );
        }
        User senderUser;
        String senderTypeName;

        Long customerUserId =
                chat.getCustomer().getUser().getUserId();

        Agent assignedAgent = chat.getAssignedAgent();

        // A method chain does not stop when an intermediate value is null. If
        // getAssignedAgent() returns null, calling getUser() on it throws a
        // NullPointerException before the result can be assigned or checked later.
        // Store the agent first so it can be checked before calling its methods.
        Long agentUserId = assignedAgent == null
                ? null
                : assignedAgent.getUser().getUserId();

        if (senderUserId.equals(customerUserId)) {
            senderUser = chat.getCustomer().getUser();
            senderTypeName = "CUSTOMER";
        } else if (agentUserId != null && senderUserId.equals(agentUserId)) {
            senderUser = assignedAgent.getUser();
            senderTypeName = "AGENT";
        } else {
            throw new ChatAccessDeniedException(chatId, senderUserId);
        }

        Optional<ChatMessage> existingMessage =
                chatMessageRepository.findByChatSession_ChatIdAndSenderUser_UserIdAndClientMessageId(
                        chatId,
                        senderUserId,
                        request.clientMessageId()
                );
        // Check whether this sender has already submitted the same client message
        // in this chat. If found, this is a retry, so return the existing saved
        // message instead of inserting a duplicate row.
        if (existingMessage.isPresent()) {
            return toResponse(existingMessage.get());
        }

            SenderType senderType = senderTypeRepository
                    .findBySender(senderTypeName)
                    .orElseThrow(() -> new ReferenceDataNotFoundException(
                            "Required sender type not found: " + senderTypeName
                    ));

            ChatMessage message = new ChatMessage();
            message.setChatSession(chat);
            message.setSenderUser(senderUser);
            message.setSenderType(senderType);
            message.setMessageType("TEXT");
            message.setContent(request.content());
            message.setClientMessageId(request.clientMessageId());
            message.setCreatedAt(LocalDateTime.now());

            ChatMessage savedMessage =
                    chatMessageRepository.save(message);

            return toResponse(savedMessage);
        }

    public ChatMessageResponse toResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getMessageId(),
                message.getClientMessageId(),
                message.getChatSession().getChatId(),
                message.getSenderUser() == null ? null : message.getSenderUser().getUserId(),
                message.getSenderType().getSender(),
                message.getMessageType(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getRecentMessages(
            Long chatId,
            Long userId
    ) {
        ChatSession chat = chatSessionRepository
                .findById(chatId)
                .orElseThrow(()-> new ChatNotFoundException(chatId));

        Long customerUserId =
                chat.getCustomer().getUser().getUserId();

        Agent assignedAgent = chat.getAssignedAgent();

        Long agentUserId = assignedAgent == null
                ? null
                : assignedAgent.getUser().getUserId();

        boolean isCustomer = customerUserId.equals(userId);
        boolean isAgent = agentUserId != null && agentUserId.equals(userId);

        if (!isCustomer && !isAgent) {
            throw new ChatAccessDeniedException(chatId, userId);
        }

        List<ChatMessage> recentMessages =
                chatMessageRepository
                        .findTop100ByChatSession_ChatIdOrderByMessageIdDesc(
                                chatId
                        );

        return recentMessages.reversed()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
