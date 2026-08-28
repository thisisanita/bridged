package com.anita.bridged.repository;

import com.anita.bridged.entity.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatStatusRepository extends JpaRepository<ChatStatus, Long> {

    Optional<ChatStatus> findByStatus(String status);
}
