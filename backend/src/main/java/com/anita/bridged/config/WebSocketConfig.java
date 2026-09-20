package com.anita.bridged.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

// Enables STOMP messaging over WebSocket, exposes /ws for client connections,
// routes /app messages to backend handlers, and broadcasts /topic messages
// to subscribed clients. Local frontend origins are allowed for development.
    @Override
    public void configureMessageBroker (MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints (StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:*");
    }
/*
    Browser connects to:
    ws://localhost:8080/ws

    Browser subscribes to:
            /topic/chats/57
            ↑
            │ broadcasts saved message
    Spring broker
              ↑
                      │
    Backend saves message to PostgreSQL
              ↑
                      │ handles incoming message
    Browser sends to:
            /app/chats/57/messages*/
}
