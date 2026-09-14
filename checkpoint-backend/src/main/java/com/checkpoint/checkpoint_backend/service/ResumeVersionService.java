package com.checkpoint.checkpoint_backend.service;


import com.checkpoint.checkpoint_backend.dto.SaveVersionRequest;
import com.checkpoint.checkpoint_backend.model.BaseResume;
import com.checkpoint.checkpoint_backend.model.ResumeVersion;
import com.checkpoint.checkpoint_backend.model.User;
import com.checkpoint.checkpoint_backend.repository.BaseResumeRepository;
import com.checkpoint.checkpoint_backend.repository.ResumeVersionRepository;
import com.checkpoint.checkpoint_backend.repository.UserRepository;
import com.checkpoint.checkpoint_backend.security.RlsSessionHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeVersionService {

    private final BaseResumeRepository baseResumeRepository;
    private final UserRepository userRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public ResumeVersionService(BaseResumeRepository baseResumeRepository, UserRepository userRepository,
                                ResumeVersionRepository resumeVersionRepository) {
        this.baseResumeRepository = baseResumeRepository;
        this.userRepository = userRepository;
        this.resumeVersionRepository = resumeVersionRepository;
    }

    @Transactional
    public Map<String, Object> saveVersion(String userEmail, UUID resumeId, SaveVersionRequest request) {
        RlsSessionHelper.applyCurrentUser(entityManager);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BaseResume resume = baseResumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have access to this resume");
        }

        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("summary", request.getSummary());
        try {
            snapshot.put("skills", objectMapper.readValue(request.getSkills(), List.class));
            snapshot.put("experienceBullets", objectMapper.readValue(request.getExperienceBullets(), List.class));
        } catch (Exception e) {
            snapshot.put("skills", new ArrayList<>());
            snapshot.put("experienceBullets", new ArrayList<>());
        }

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

    @Transactional
    public List<Map<String, Object>> getVersionHistory(String userEmail, UUID resumeId) {
        RlsSessionHelper.applyCurrentUser(entityManager);
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

    @Transactional
    public java.util.Map<String, Object> getVersionDetail(String userEmail, java.util.UUID resumeId, java.util.UUID versionId) {
        RlsSessionHelper.applyCurrentUser(entityManager);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ResumeVersion version = resumeVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        if (!version.getBaseResume().getUser().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("No access");
        }

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("id", version.getId());
        result.put("label", version.getLabel());
        result.put("atsScore", version.getAtsScore());
        result.put("dynamicFieldsSnapshot", version.getDynamicFieldsSnapshot());
        result.put("createdAt", version.getCreatedAt());
        return result;
    }

    @Transactional
    public void restoreVersion(String userEmail, java.util.UUID resumeId, java.util.UUID versionId) {
        RlsSessionHelper.applyCurrentUser(entityManager);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ResumeVersion version = resumeVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        BaseResume resume = version.getBaseResume();

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("No access");
        }

        resume.setCurrentDynamicFields(version.getDynamicFieldsSnapshot());
        baseResumeRepository.save(resume);
    }

    @Transactional
    public List<Map<String, Object>> getRecentForUser(String userEmail) {
        RlsSessionHelper.applyCurrentUser(entityManager);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return resumeVersionRepository.findTop5ByBaseResume_User_IdOrderByCreatedAtDesc(user.getId())
                .stream().map(v -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", v.getId());
                    m.put("baseResumeId", v.getBaseResume().getId());
                    m.put("label", v.getLabel());
                    m.put("atsScore", v.getAtsScore());
                    m.put("createdAt", v.getCreatedAt());
                    return m;
                }).collect(Collectors.toList());
    }

}
