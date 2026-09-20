package com.anita.bridged.dto;

public class CreateChatResponse {
    private Long chatId;
    private Long customerId;
    private String customerName;
    private String status;
    private String preferredLanguage;
    private ChatMessageResponse initialMessage;

    public CreateChatResponse(Long chatId, Long customerId, String customerName, String status, String preferredLanguage, ChatMessageResponse initialMessage) {
        this.chatId = chatId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.status = status;
        this.preferredLanguage = preferredLanguage;
        this.initialMessage = initialMessage;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStatus() {
        return status;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public ChatMessageResponse getInitialMessage() {
        return initialMessage;
    }
}
