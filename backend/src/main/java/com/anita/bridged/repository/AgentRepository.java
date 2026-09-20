package com.anita.bridged.repository;

import com.anita.bridged.entity.Agent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    @Query(
            value = """
            SELECT a.*
            FROM agent a
            JOIN availability_status av
                ON av.availability_id = a.availability_status_id
            JOIN agent_skill ags
                ON ags.agent_id = a.agent_id
            JOIN agent_language agl
                ON agl.agent_id = a.agent_id
            JOIN proficiency p_skill
                ON p_skill.proficiency_id = ags.proficiency_id
            JOIN proficiency p_lang
                ON p_lang.proficiency_id = agl.proficiency_id
            WHERE av.availability = 'AVAILABLE'
              AND a.open_chat_count < a.max_open_chats
              AND ags.skill_id = :skillId
              AND agl.language_id = :languageId
            ORDER BY
                (
                    cast(a.open_chat_count as decimal)
                    / NULLIF(a.max_open_chats, 0)
                ) ASC,
                p_skill.proficiency_level DESC,
                p_lang.proficiency_level DESC,
                a.agent_id ASC
            LIMIT 1
            FOR UPDATE OF a SKIP LOCKED
            """,
            nativeQuery = true
    )
    Optional<Agent> findBestEligibleAgent(
            @Param("skillId") Long skillId,
            @Param("languageId") Long languageId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT a
    FROM Agent a
    WHERE a.agentId = :agentId
    """)
    Optional<Agent> findByIdForUpdate(
            @Param("agentId") Long agentId
    );

    Optional<Agent> findByUser_UserId(Long userId);
}