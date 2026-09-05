package com.anita.bridged.service;

import com.anita.bridged.dto.AssignedChatResponse;
import com.anita.bridged.entity.Agent;
import com.anita.bridged.entity.ChatSession;
import com.anita.bridged.entity.ChatStatus;
import com.anita.bridged.exception.ReferenceDataNotFoundException;
import com.anita.bridged.repository.AgentRepository;
import com.anita.bridged.repository.ChatSessionRepository;
import com.anita.bridged.repository.ChatStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AssignmentService {

    private final AgentRepository agentRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatStatusRepository chatStatusRepository;

    public AssignmentService(AgentRepository agentRepository,
                             ChatSessionRepository chatSessionRepository,
                             ChatStatusRepository chatStatusRepository) {
        this.agentRepository = agentRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.chatStatusRepository = chatStatusRepository;
    }

    //transaction so that all updates happens in one transaction before lock is released
    @Transactional
    public Optional<AssignedChatResponse> assignNextChat() {
        //Find the next chat to be assigned
        Optional<ChatSession> optionalChat = chatSessionRepository.findNextAssignableChat();

        if (optionalChat.isEmpty()) {
            return Optional.empty();
        }

        ChatSession chatSession = optionalChat.get();

        //Find the best agent for the chat

        Optional<Agent> optionalAgent =
                agentRepository.findBestEligibleAgent(chatSession.getTopicSkill().getSkillId(),
                                                      chatSession.getPreferredLanguage().getLanguageId());


        if (optionalAgent.isEmpty()) {
            return Optional.empty();
        }

        Agent agent  = optionalAgent.get();

        ChatStatus assignedStatus = chatStatusRepository
                .findByStatus("ASSIGNED")
                .orElseThrow(() -> new ReferenceDataNotFoundException(
                        "Required chat status not found: ASSIGNED"
                ));

        LocalDateTime assignedAt = LocalDateTime.now();
        chatSession.setAssignedAgent(agent);
        chatSession.setChatStatus(assignedStatus);
        chatSession.setAssignedAt(assignedAt);
        agent.setOpenChatCount(agent.getOpenChatCount() + 1);

       AssignedChatResponse assignedChatResponse = new AssignedChatResponse(
               chatSession.getChatId(),
               chatSession.getCustomer().getCustomerId(),
               agent.getAgentId(),
               agent.getFullName(),
               chatSession.getChatStatus().getStatus(),
               chatSession.getPriority().getPriority(),
               chatSession.getTopicSkill().getSkillName(),
               chatSession.getPreferredLanguage().getLanguage(),
               chatSession.getAssignedAt(),
               chatSession.getAssignmentDueAt()
       );

        return Optional.of(assignedChatResponse);
    }
}
