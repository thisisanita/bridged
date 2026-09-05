package com.anita.bridged.repository;

import com.anita.bridged.entity.ChatSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

   /* findBy
      ChatStatus_Status
      OrderBy
      AssignmentDueAt
      Asc */
    List<ChatSession> findByChatStatus_StatusOrderByAssignmentDueAtAscTriageCompletedAtAscChatIdAsc(String status);

    @Query(
            value= """
              select cs.*
              from chat_session cs
              join chat_status ch 
              on ch.chat_status_id = cs.chat_status_id 
              where ch.status = 'WAITING'
              and cs.assignment_due_at is not null
              and exists (select 1 
              from agent a 
              join availability_status av
              on av.availability_id = a.availability_status_id 
              join agent_skill ags
              on ags.agent_id = a.agent_id 
              join agent_language agl
              on agl.agent_id = a.agent_id 
              where av.availability = 'AVAILABLE'
              and a.open_chat_count < a.max_open_chats
              and ags.skill_id = cs.topic_skill_id 
              and agl.language_id = cs.preferred_language_id 
              )
              order by 
              cs.assignment_due_at asc,
              cs.triage_completed_at asc,
              cs.chat_id asc
              limit 1
              for update of cs skip locked
              """,
            nativeQuery = true
    )
    Optional<ChatSession> findNextAssignableChat();

    //This repository method finds one specific chat and locks it
    // so only one transaction can change its lifecycle at a time.
    //Load this chat and reserve it for updating until the current transaction finishes.
    //“Pessimistic” means we assume another request might try to update the same row,
    // so we lock it before making changes.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT c
    FROM ChatSession c
    WHERE c.chatId = :chatId
    """)
    Optional<ChatSession> findByIdForUpdate(
            @Param("chatId") Long chatId
    );

 @Query("""
    SELECT c
    FROM ChatSession c
    WHERE c.assignedAgent.agentId = :agentId
      AND c.chatStatus.status IN ('ASSIGNED', 'ACTIVE')
    ORDER BY c.assignedAt ASC, c.chatId ASC
    """)
 List<ChatSession> findOpenChatsByAgentId(
         @Param("agentId") Long agentId
 );

}
