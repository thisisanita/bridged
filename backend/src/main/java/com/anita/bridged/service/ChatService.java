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
    private final ChatMessageRepository chatMessageRepository;
    private final SenderTypeRepository senderTypeRepository;
    private final ChatMessageService chatMessageService;

    public ChatService(ChatSessionRepository chatSessionRepository, CustomerRepository customerRepository, ChatStatusRepository chatStatusRepository, SkillRepository skillRepository, LanguageRepository languageRepository, AgentRepository agentRepository, ChatMessageRepository chatMessageRepository, SenderTypeRepository senderTypeRepository, ChatMessageService chatMessageService) {
        this.chatSessionRepository = chatSessionRepository;
        this.customerRepository = customerRepository;
        this.chatStatusRepository = chatStatusRepository;
        this.skillRepository = skillRepository;
        this.languageRepository = languageRepository;
        this.agentRepository = agentRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.senderTypeRepository = senderTypeRepository;
        this.chatMessageService = chatMessageService;
    }

    //Create Chat
    @Transactional
    public CreateChatResponse createChat(Long customerId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        ChatStatus triageStatus = chatStatusRepository
                .findByStatus("TRIAGE")
                .orElseThrow(() -> new ReferenceDataNotFoundException("Required chat status not found: TRIAGE"));

        SenderType botSenderType = senderTypeRepository
                .findBySender("BOT")
                .orElseThrow(()-> new ReferenceDataNotFoundException(
                        "Required sender type not found: BOT"
                ));

        ChatSession chatSession = new ChatSession();
        chatSession.setCustomer(customer);
        chatSession.setChatStatus(triageStatus);

        ChatSession savedChatSession = chatSessionRepository.save(chatSession);

        ChatMessage languagePrompt = new ChatMessage();
        languagePrompt.setChatSession(savedChatSession);
        languagePrompt.setSenderType(botSenderType);
        languagePrompt.setSenderUser(null);
        languagePrompt.setMessageType("BOT_MESSAGE");
        languagePrompt.setContent("Which language would you prefer?");
        languagePrompt.setCreatedAt(LocalDateTime.now());

        ChatMessage savedLanguagePrompt = chatMessageRepository.save(languagePrompt);


        CreateChatResponse createChatResponse = new CreateChatResponse(savedChatSession.getChatId(),
                                                                        savedChatSession.getCustomer().getCustomerId(),
                                                                        savedChatSession.getCustomer().getFullName(),
                                                                        savedChatSession.getChatStatus().getStatus(),
                                                                        null,
                                                                        chatMessageService.toResponse(savedLanguagePrompt)

        );

        return createChatResponse;

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

    @Transactional
    public SelectLanguageResponse selectLanguage(
            Long chatId,
            SelectLanguageRequest request
    ) {
        ChatSession chatSession = chatSessionRepository
                .findByIdForUpdate(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        String currentStatus = chatSession.getChatStatus().getStatus();

        if (!"TRIAGE".equals(currentStatus)) {
            throw new InvalidChatStateException(
                    "Chat " + chatId
                            + " cannot select a language because it is currently "
                            + currentStatus
            );
        }

        if (chatSession.getPreferredLanguage() != null) {
            throw new InvalidTriageSelectionException(
                    "A language has already been selected for chat " + chatId
            );
        }

        Language selectedLanguage = languageRepository
                .findByLanguage(request.language())
                .orElseThrow(() -> new InvalidTriageSelectionException(
                        "Invalid triage language: " + request.language()
                ));

        chatSession.setPreferredLanguage(selectedLanguage);

        SenderType customerSenderType = senderTypeRepository
                .findBySender("CUSTOMER")
                .orElseThrow(() -> new ReferenceDataNotFoundException(
                        "Required sender type not found: CUSTOMER"
                ));

        ChatMessage customerLanguageMessage = new ChatMessage();
        customerLanguageMessage.setChatSession(chatSession);
        customerLanguageMessage.setSenderUser(chatSession.getCustomer().getUser());
        customerLanguageMessage.setSenderType(customerSenderType);
        customerLanguageMessage.setMessageType("TEXT");
        customerLanguageMessage.setCreatedAt(LocalDateTime.now());
        customerLanguageMessage.setContent(selectedLanguage.getLanguage());

        ChatMessage savedCustomerLanguageMessage = chatMessageRepository.save(customerLanguageMessage);

        SenderType botSenderType = senderTypeRepository
                .findBySender("BOT")
                .orElseThrow(() -> new ReferenceDataNotFoundException(
                        "Required sender type not found: BOT"
                ));

        ChatMessage topicPrompt = new ChatMessage();
        topicPrompt.setChatSession(chatSession);
        topicPrompt.setSenderUser(null);
        topicPrompt.setSenderType(botSenderType);
        topicPrompt.setMessageType("BOT_MESSAGE");
        topicPrompt.setContent("What do you need help with today?");
        topicPrompt.setCreatedAt(LocalDateTime.now());

        ChatMessage savedTopicPrompt = chatMessageRepository.save(topicPrompt);

        return new SelectLanguageResponse(
                chatSession.getChatId(),
                chatSession.getChatStatus().getStatus(),
                selectedLanguage.getLanguage(),
                List.of(
                        chatMessageService.toResponse(savedCustomerLanguageMessage),
                        chatMessageService.toResponse(savedTopicPrompt)
                )
        );
    }

    @Transactional
    public SelectTopicResponse selectTopic(
            Long chatId,
            SelectTopicRequest request
    ) {
        ChatSession chatSession = chatSessionRepository
                .findByIdForUpdate(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        String currentStatus = chatSession.getChatStatus().getStatus();

        if (!"TRIAGE".equals(currentStatus)) {
            throw new InvalidChatStateException(
                    "Chat " + chatId
                            + " cannot select a topic because it is currently "
                            + currentStatus
            );
        }

        if (chatSession.getPreferredLanguage() == null) {
            throw new InvalidTriageSelectionException(
                    "A language must be selected before choosing a topic"
            );
        }

        if (chatSession.getTopicSkill() != null) {
            throw new InvalidTriageSelectionException(
                    "A topic has already been selected for chat " + chatId
            );
        }

        Skill selectedSkill = skillRepository
                .findBySkillName(request.skillName())
                .orElseThrow(() -> new InvalidTriageSelectionException(
                        "Invalid triage topic: " + request.skillName()
                ));

        ChatStatus waitingStatus = chatStatusRepository
                .findByStatus("WAITING")
                .orElseThrow(() -> new ReferenceDataNotFoundException(
                        "Required chat status not found: WAITING"
                ));

        LocalDateTime triageCompletedAt = LocalDateTime.now();
        LocalDateTime assignmentDueAt = triageCompletedAt.plusMinutes(selectedSkill.getPriority().getAssignmentTargetMinutes());

        chatSession.setTopicSkill(selectedSkill);
        chatSession.setPriority(selectedSkill.getPriority());
        chatSession.setChatStatus(waitingStatus);
        chatSession.setTriageCompletedAt(triageCompletedAt);
        chatSession.setAssignmentDueAt(assignmentDueAt);

        SenderType customerSenderType = senderTypeRepository
                .findBySender("CUSTOMER")
                .orElseThrow(()-> new ReferenceDataNotFoundException(
                        "Required sender type not found: CUSTOMER"
                ));

        ChatMessage customerTopicMessage = new ChatMessage();
        customerTopicMessage.setChatSession(chatSession);
        customerTopicMessage.setSenderUser(chatSession.getCustomer().getUser());
        customerTopicMessage.setSenderType(customerSenderType);
        customerTopicMessage.setMessageType("TEXT");
        customerTopicMessage.setContent(selectedSkill.getSkillName());
        customerTopicMessage.setCreatedAt(LocalDateTime.now());

        ChatMessage savedCustomerTopicMessage = chatMessageRepository.save(customerTopicMessage);

        SenderType botSenderType = senderTypeRepository
                .findBySender("BOT")
                .orElseThrow(() -> new ReferenceDataNotFoundException(
                        "Required sender type not found: BOT"
                ));

        ChatMessage waitingMessage = new ChatMessage();
        waitingMessage.setChatSession(chatSession);
        waitingMessage.setSenderUser(null);
        waitingMessage.setSenderType(botSenderType);
        waitingMessage.setMessageType("BOT_MESSAGE");
        waitingMessage.setContent(
                "We are finding the right agent for you now."
        );

        waitingMessage.setCreatedAt(LocalDateTime.now());

        ChatMessage savedWaitingMessage = chatMessageRepository.save(waitingMessage);

        return new SelectTopicResponse(
                chatSession.getChatId(),
                chatSession.getChatStatus().getStatus(),
                selectedSkill.getSkillName(),
                selectedSkill.getPriority().getPriority(),
                chatSession.getPreferredLanguage().getLanguage(),
                triageCompletedAt,
                assignmentDueAt,
                List.of(
                        chatMessageService.toResponse(savedCustomerTopicMessage),
                        chatMessageService.toResponse(savedWaitingMessage)
                )
        );
    }

}
