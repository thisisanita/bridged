package com.anita.bridged.repository;

import com.anita.bridged.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

   /* findBy
      ChatStatus_Status
      OrderBy
      TriageCompletedAt
      Asc */
    List<ChatSession> findByChatStatus_StatusOrderByTriageCompleted(String status);

}
