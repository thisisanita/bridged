package com.anita.bridged.repository;

import com.anita.bridged.entity.SenderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SenderTypeRepository extends JpaRepository<SenderType, Long> {

    Optional<SenderType> findBySender(String sender);

}
