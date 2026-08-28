package com.anita.bridged.service;

import com.anita.bridged.dto.CompleteTriageRequest;
import com.anita.bridged.dto.CompleteTriageResponse;
import com.anita.bridged.dto.CreateChatResponse;
import com.anita.bridged.entity.*;
import com.anita.bridged.exception.*;
import com.anita.bridged.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final CustomerRepository customerRepository;
    private final ChatStatusRepository chatStatusRepository;
    private final SkillRepository skillRepository;
    private final LanguageRepository languageRepository;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       CustomerRepository customerRepository,
                       ChatStatusRepository chatStatusRepository,
                       SkillRepository skillRepository,
                       LanguageRepository languageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.customerRepository = customerRepository;
        this.chatStatusRepository = chatStatusRepository;
        this.skillRepository = skillRepository;
        this.languageRepository = languageRepository;
    }

    //Create Chat
    public CreateChatResponse createChat(Long customerId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        ChatStatus triageStatus = chatStatusRepository
                .findByStatus("TRIAGE")
                .orElseThrow(() -> new ReferenceDataNotFoundException("Required chat status not found: TRIAGE"));

        ChatSession chatSession = new ChatSession();
        chatSession.setCustomer(customer);
        chatSession.setChatStatus(triageStatus);
        chatSession.setPreferredLanguage(customer.getPreferredLanguage());

        ChatSession savedChatSession = chatSessionRepository.save(chatSession);
        CreateChatResponse createChatResponse = new CreateChatResponse(savedChatSession.getChatId(),
                                                                        savedChatSession.getCustomer().getCustomerId(),
                                                                        savedChatSession.getCustomer().getFullName(),
                                                                        savedChatSession.getChatStatus().getStatus(),
                                                                        savedChatSession.getPreferredLanguage().getLanguage()
        );

        return createChatResponse;

    }

    //Complete Triage
    public CompleteTriageResponse completeTriage (Long chatId, CompleteTriageRequest request) {
        ChatSession chatSession = chatSessionRepository
                .findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        if (!chatSession.getChatStatus().getStatus().equals("TRIAGE")) {
            throw new InvalidChatStateException("Chat " + chatId + " cannot complete triage because it is currently "
            + chatSession.getChatStatus().getStatus());
        }

        Skill skill = skillRepository
                .findBySkillName(request.getSkillName())
                .orElseThrow(() -> new InvalidTriageSelectionException("Invalid triage skill: " + request.getSkillName()));

        Language language = languageRepository
                .findByLanguage(request.getLanguage())
                .orElseThrow(() -> new InvalidTriageSelectionException("Invalid triage language: " + request.getLanguage()));


        ChatStatus chatStatus = chatStatusRepository
                .findByStatus("WAITING")
                .orElseThrow(() -> new ReferenceDataNotFoundException("Required chat status not found: WAITING"));

        chatSession.setTopicSkill(skill);
        chatSession.setPreferredLanguage(language);
        chatSession.setChatStatus(chatStatus);
        chatSession.setPriority(skill.getPriority());
        chatSession.setTriageCompletedAt(LocalDateTime.now());

        ChatSession savedChatSession = chatSessionRepository.save(chatSession);

        CompleteTriageResponse completeTriageResponse = new CompleteTriageResponse(
                savedChatSession.getChatId(),
                savedChatSession.getChatStatus().getStatus(),
                savedChatSession.getTopicSkill().getSkillName(),
                savedChatSession.getPriority().getPriority(),
                savedChatSession.getPreferredLanguage().getLanguage(),
                savedChatSession.getTriageCompletedAt()
        );

        return completeTriageResponse;
    }
}
