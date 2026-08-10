package com.checkpoint.checkpoint_backend.service;


import com.checkpoint.checkpoint_backend.dto.SaveVersionRequest;
import com.checkpoint.checkpoint_backend.model.BaseResume;
import com.checkpoint.checkpoint_backend.model.ResumeVersion;
import com.checkpoint.checkpoint_backend.model.User;
import com.checkpoint.checkpoint_backend.repository.BaseResumeRepository;
import com.checkpoint.checkpoint_backend.repository.ResumeVersionRepository;
import com.checkpoint.checkpoint_backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeVersionService {

    private final BaseResumeRepository baseResumeRepository;
    private final UserRepository userRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeVersionService(BaseResumeRepository baseResumeRepository, UserRepository userRepository,
                                ResumeVersionRepository resumeVersionRepository) {
        this.baseResumeRepository = baseResumeRepository;
        this.userRepository = userRepository;
        this.resumeVersionRepository = resumeVersionRepository;
    }

    public Map<String, Object> saveVersion(String userEmail, UUID resumeId, SaveVersionRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BaseResume resume = baseResumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have access to this resume");
        }

        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("summary", request.getSummary());
        snapshot.put("skills", request.getSkills());
        snapshot.put("experienceBullets", request.getExperienceBullets());

        ResumeVersion version = new ResumeVersion();
        version.setBaseResume(resume);
        version.setJdText(request.getJdText());
        version.setAtsScore(request.getAtsScore());
        version.setLabel(request.getLabel());

        try {
            version.setDynamicFieldsSnapshot(objectMapper.writeValueAsString(snapshot));
        } catch (Exception e) {
            version.setDynamicFieldsSnapshot("{}");
        }

        resumeVersionRepository.save(version);

        Map<String, Object> result = new HashMap<>();
        result.put("id", version.getId());
        result.put("createdAt", version.getCreatedAt());
        return result;
    }

    public List<Map<String, Object>> getVersionHistory(String userEmail, UUID resumeId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BaseResume resume = baseResumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have access to this resume");
        }

        return resumeVersionRepository.findByBaseResumeIdOrderByCreatedAtDesc(resumeId)
                .stream().map(v -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", v.getId());
                    m.put("label", v.getLabel());
                    m.put("atsScore", v.getAtsScore());
                    m.put("createdAt", v.getCreatedAt());
                    return m;
                }).collect(Collectors.toList());
    }
}
