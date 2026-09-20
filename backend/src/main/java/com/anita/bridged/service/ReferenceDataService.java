package com.anita.bridged.service;

import com.anita.bridged.dto.LanguageOptionResponse;
import com.anita.bridged.dto.SkillOptionResponse;
import com.anita.bridged.repository.LanguageRepository;
import com.anita.bridged.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ReferenceDataService {

    private final LanguageRepository languageRepository;
    private final SkillRepository skillRepository;

    public ReferenceDataService(
            LanguageRepository languageRepository,
            SkillRepository skillRepository
    ) {
        this.languageRepository = languageRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<LanguageOptionResponse> listLanguages() {
        return languageRepository
                .findAllByOrderByLanguageAsc()
                .stream()
                .map(language -> new LanguageOptionResponse(
                        language.getLanguage(),
                        toLabel(language.getLanguage())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SkillOptionResponse> listSkills() {
        return skillRepository
                .findAllByOrderBySkillNameAsc()
                .stream()
                .map(skill -> new SkillOptionResponse(
                        skill.getSkillName(),
                        toLabel(skill.getSkillName()),
                        skill.getDescription()
                ))
                .toList();
    }

    private String toLabel(String code) {
        String words = code
                .toLowerCase(Locale.ROOT)
                .replace('_', ' ');

        return Character.toUpperCase(words.charAt(0))
                + words.substring(1);
    }
}