package com.anita.bridged.controller;

import com.anita.bridged.dto.ChatMessageResponse;
import com.anita.bridged.dto.SendMessageRequest;
import com.anita.bridged.service.ChatMessageService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

//use @controller instead of @RestController as this is a STOMP controller
@Controller
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    //Spring’s tool for sending messages to STOMP destinations from Java code.
    //Convert savedMessage into JSON and send it to every client subscribed to /topic/chats/{chatId}.
    /*savedMessage
     ↓
    SimpMessagingTemplate
     ↓
             /topic/chats/57
            ↓
    customer and assigned agent*/
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageController(ChatMessageService chatMessageService, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    // @MessageMapping Tells Spring the method handles incoming STOMP messages.
    // Defines the destination pattern handled by this method.
    @MessageMapping(
            "/chats/{chatId}/users/{senderUserId}/messages"
    )

    /*
    A destination variable is value extracted from a STOMP destination,
    the WebSocket equivalent of a REST @PathVariable.
    For now, senderUserId tells backend who is sending the message.
    It is temporary because a client could falsify it.
    Production code will derive this value from authentication.
    @Valid checks the DTO rules before processing it,
    including @NotNull, @NotBlank, and the 4,000-character limit.
     */
    public void sendMessage(
            @DestinationVariable Long chatId,
            @DestinationVariable Long senderUserId,
            @Valid SendMessageRequest request
    ) {
        ChatMessageResponse savedMessage =
                chatMessageService.saveMessage(
                        chatId,
                        senderUserId,
                        request
                );
        //Asks Spring’s STOMP messaging system to broadcast a message.
        messagingTemplate.convertAndSend(
                "/topic/chats/" + chatId,
                savedMessage
        );
    }

   /* Frontend sends to /app/chats/57/users/10/messages
                    ↓
    Spring extracts chatId=57 and senderUserId=10
                    ↓
    JSON becomes SendMessageRequest
                    ↓
    Service validates and saves the message
                    ↓
    Controller broadcasts to /topic/chats/57
            ↓
    Customer and agent subscribers receive the saved message*/
}
