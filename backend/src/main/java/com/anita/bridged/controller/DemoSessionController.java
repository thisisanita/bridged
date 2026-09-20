package com.anita.bridged.controller;

import com.anita.bridged.dto.DemoSessionRequest;
import com.anita.bridged.dto.DemoSessionResponse;
import com.anita.bridged.service.DemoSessionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoSessionController {

    private final DemoSessionService demoSessionService;

    public DemoSessionController(
            DemoSessionService demoSessionService
    ) {
        this.demoSessionService = demoSessionService;
    }

    @PostMapping("/session")
    public DemoSessionResponse createSession(
            @Valid @RequestBody DemoSessionRequest request
    ) {
        return demoSessionService.createSession(request);
    }

}
