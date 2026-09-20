package com.anita.bridged.service;

import com.anita.bridged.dto.DemoSessionRequest;
import com.anita.bridged.dto.DemoSessionResponse;
import com.anita.bridged.entity.Agent;
import com.anita.bridged.entity.Customer;
import com.anita.bridged.entity.User;
import com.anita.bridged.exception.InvalidDemoSessionException;
import com.anita.bridged.repository.AgentRepository;
import com.anita.bridged.repository.CustomerRepository;
import com.anita.bridged.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class DemoSessionService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;

    public DemoSessionService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            AgentRepository agentRepository
    ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.agentRepository = agentRepository;
    }

    @Transactional(readOnly = true)
    public DemoSessionResponse createSession(
            DemoSessionRequest request
    ) {
        String requestedRole =
                request.role().trim().toUpperCase(Locale.ROOT);

        if (!"CUSTOMER".equals(requestedRole)
                && !"AGENT".equals(requestedRole)) {
            throw new InvalidDemoSessionException(
                    "Role must be CUSTOMER or AGENT"
            );
        }

        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() -> new InvalidDemoSessionException(
                        "User not found with id: " + request.userId()
                ));

        if (!"ACTIVE".equals(user.getStatus().getUserStatus())) {
            throw new InvalidDemoSessionException(
                    "User " + user.getUserId() + " is not active"
            );
        }

        String storedRole = user.getRole().getUserRole();

        if (!requestedRole.equals(storedRole)) {
            throw new InvalidDemoSessionException(
                    "User " + user.getUserId()
                            + " has role " + storedRole
                            + ", not " + requestedRole
            );
        }

        if ("CUSTOMER".equals(requestedRole)) {
            Customer customer = customerRepository
                    .findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new InvalidDemoSessionException(
                            "Customer profile not found for user "
                                    + user.getUserId()
                    ));

            return new DemoSessionResponse(
                    user.getUserId(),
                    requestedRole,
                    customer.getCustomerId(),
                    null,
                    customer.getFullName()
            );
        }

        Agent agent = agentRepository
                .findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new InvalidDemoSessionException(
                        "Agent profile not found for user "
                                + user.getUserId()
                ));

        return new DemoSessionResponse(
                user.getUserId(),
                requestedRole,
                null,
                agent.getAgentId(),
                agent.getFullName()
        );
    }
}
