package com.anita.bridged.controller;

import com.anita.bridged.dto.LanguageOptionResponse;
import com.anita.bridged.dto.SkillOptionResponse;
import com.anita.bridged.service.ReferenceDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reference")
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    public ReferenceDataController(
            ReferenceDataService referenceDataService
    ) {
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/languages")
    public List<LanguageOptionResponse> listLanguages() {
        return referenceDataService.listLanguages();
    }

    @GetMapping("/skills")
    public List<SkillOptionResponse> listSkills() {
        return referenceDataService.listSkills();
    }
}