package com.anita.bridged.service;

import com.anita.bridged.dto.*;
import com.anita.bridged.entity.*;
import com.anita.bridged.exception.*;
import com.anita.bridged.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final CustomerRepository customerRepository;
    private final ChatStatusRepository chatStatusRepository;
    private final SkillRepository skillRepository;
    private final LanguageRepository languageRepository;
    private final AgentRepository agentRepository;

    public ChatService(ChatSessionRepository chatSessionRepository, CustomerRepository customerRepository, ChatStatusRepository chatStatusRepository, SkillRepository skillRepository, LanguageRepository languageRepository, AgentRepository agentRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.customerRepository = customerRepository;
        this.chatStatusRepository = chatStatusRepository;
        this.skillRepository = skillRepository;
        this.languageRepository = languageRepository;
        this.agentRepository = agentRepository;
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

        LocalDateTime triageCompletedAt = LocalDateTime.now();
        LocalDateTime assignmentDueAt = triageCompletedAt.plusMinutes(skill.getPriority().getAssignmentTargetMinutes());

        chatSession.setTopicSkill(skill);
        chatSession.setPreferredLanguage(language);
        chatSession.setChatStatus(chatStatus);
        chatSession.setPriority(skill.getPriority());
        chatSession.setTriageCompletedAt(triageCompletedAt);
        chatSession.setAssignmentDueAt(assignmentDueAt);

        ChatSession savedChatSession = chatSessionRepository.save(chatSession);

        CompleteTriageResponse completeTriageResponse = new CompleteTriageResponse(
                savedChatSession.getChatId(),
                savedChatSession.getChatStatus().getStatus(),
                savedChatSession.getTopicSkill().getSkillName(),
                savedChatSession.getPriority().getPriority(),
                savedChatSession.getPreferredLanguage().getLanguage(),
                savedChatSession.getTriageCompletedAt(),
                savedChatSession.getAssignmentDueAt()
        );

        return completeTriageResponse;
    }

    public List<WaitingChatResponse> listWaitingChats() {
        List<ChatSession> waitingChats = chatSessionRepository.
                findByChatStatus_StatusOrderByAssignmentDueAtAscTriageCompletedAtAscChatIdAsc("WAITING");

        return waitingChats.stream()
                .map(chatSession -> new WaitingChatResponse(
                        chatSession.getChatId(),
                        chatSession.getCustomer().getCustomerId(),
                        chatSession.getChatStatus().getStatus(),
                        chatSession.getTopicSkill().getSkillName(),
                        chatSession.getPriority().getPriority(),
                        chatSession.getPreferredLanguage().getLanguage(),
                        chatSession.getTriageCompletedAt(),
                        chatSession.getCreatedAt(),
                        chatSession.getAssignmentDueAt()
                        ))
                .toList();
        }

        //Agent to accept the chat that is assigned to them
        @Transactional
        public AcceptChatResponse acceptChat(Long chatId) {
            ChatSession chatSession = chatSessionRepository
                    .findByIdForUpdate(chatId)
                    .orElseThrow(() -> new ChatNotFoundException(chatId));

            String currentStatus =
                    chatSession.getChatStatus().getStatus();

            if (!"ASSIGNED".equals(currentStatus)) {
                throw new InvalidChatStateException(
                        "Chat " + chatId
                                + " cannot be accepted because it is currently "
                                + currentStatus
                );
            }

            ChatStatus activeStatus = chatStatusRepository
                    .findByStatus("ACTIVE")
                    .orElseThrow(() -> new ReferenceDataNotFoundException(
                            "Required chat status not found: ACTIVE"
                    ));

            LocalDateTime acceptedAt = LocalDateTime.now();

            chatSession.setChatStatus(activeStatus);
            chatSession.setAcceptedAt(acceptedAt);

            return new AcceptChatResponse(
                    chatSession.getChatId(),
                    chatSession.getAssignedAgent().getAgentId(),
                    chatSession.getChatStatus().getStatus(),
                    chatSession.getAssignedAt(),
                    chatSession.getAcceptedAt()
            );
        }

    @Transactional
    public CloseChatResponse closeChat(Long chatId) {
        ChatSession chatSession = chatSessionRepository
                .findByIdForUpdate(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        String currentStatus =
                chatSession.getChatStatus().getStatus();

        if (!"ACTIVE".equals(currentStatus)) {
            throw new InvalidChatStateException(
                    "Chat " + chatId
                            + " cannot be closed because it is currently "
                            + currentStatus
            );
        }

        Agent assignedAgent = chatSession.getAssignedAgent();

        if (assignedAgent == null) {
            throw new IllegalStateException(
                    "Active chat " + chatId + " has no assigned agent"
            );
        }

        Agent lockedAgent = agentRepository
                .findByIdForUpdate(assignedAgent.getAgentId())
                .orElseThrow(() -> new IllegalStateException(
                        "Assigned agent " + assignedAgent.getAgentId()
                                + " was not found"
                ));

        if (lockedAgent.getOpenChatCount() <= 0) {
            throw new IllegalStateException(
                    "Agent " + lockedAgent.getAgentId()
                            + " has no open chats to release"
            );
        }

        ChatStatus closedStatus = chatStatusRepository
                .findByStatus("CLOSED")
                .orElseThrow(() -> new ReferenceDataNotFoundException(
                        "Required chat status not found: CLOSED"
                ));

        LocalDateTime closedAt = LocalDateTime.now();

        chatSession.setChatStatus(closedStatus);
        chatSession.setClosedAt(closedAt);

        lockedAgent.setOpenChatCount(
                lockedAgent.getOpenChatCount() - 1
        );

        return new CloseChatResponse(
                chatSession.getChatId(),
                lockedAgent.getAgentId(),
                chatSession.getChatStatus().getStatus(),
                chatSession.getClosedAt(),
                lockedAgent.getOpenChatCount()
        );
    }

    @Transactional(readOnly = true)
    public ChatDetailsResponse getChatDetails(Long chatId) {
        ChatSession chatSession = chatSessionRepository
                .findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        return toChatDetailsResponse(chatSession);
    }

    private ChatDetailsResponse toChatDetailsResponse(
            ChatSession chatSession) {

        Agent assignedAgent = chatSession.getAssignedAgent();

        Long assignedAgentId = assignedAgent == null
                ? null
                : assignedAgent.getAgentId();

        String assignedAgentName = assignedAgent == null
                ? null
                : assignedAgent.getFullName();

        String topicSkill = chatSession.getTopicSkill() == null
                ? null
                : chatSession.getTopicSkill().getSkillName();

        String priority = chatSession.getPriority() == null
                ? null
                : chatSession.getPriority().getPriority();

        String preferredLanguage =
                chatSession.getPreferredLanguage() == null
                        ? null
                        : chatSession.getPreferredLanguage().getLanguage();

        return new ChatDetailsResponse(
                chatSession.getChatId(),
                chatSession.getCustomer().getCustomerId(),
                chatSession.getCustomer().getFullName(),
                assignedAgentId,
                assignedAgentName,
                chatSession.getChatStatus().getStatus(),
                topicSkill,
                priority,
                preferredLanguage,
                chatSession.getCreatedAt(),
                chatSession.getTriageCompletedAt(),
                chatSession.getAssignmentDueAt(),
                chatSession.getAssignedAt(),
                chatSession.getAcceptedAt(),
                chatSession.getClosedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ChatDetailsResponse> listOpenChatsForAgent(
            Long agentId) {

        if (!agentRepository.existsById(agentId)) {
            throw new AgentNotFoundException(agentId);
        }

        return chatSessionRepository
                .findOpenChatsByAgentId(agentId)
                .stream()
                .map(this::toChatDetailsResponse)
                .toList();
    }

}
