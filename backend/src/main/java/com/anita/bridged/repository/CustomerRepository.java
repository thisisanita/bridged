package com.anita.bridged.repository;

import com.anita.bridged.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository <Customer, Long> {

    Optional<Customer> findByUser_UserId(Long userId);
}
