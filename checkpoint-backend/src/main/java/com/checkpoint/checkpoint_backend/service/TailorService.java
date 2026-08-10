package com.checkpoint.checkpoint_backend.service;


import com.checkpoint.checkpoint_backend.dto.TailorRequest;
import com.checkpoint.checkpoint_backend.dto.TailorResponse;
import com.checkpoint.checkpoint_backend.model.BaseResume;
import com.checkpoint.checkpoint_backend.model.User;
import com.checkpoint.checkpoint_backend.repository.BaseResumeRepository;
import com.checkpoint.checkpoint_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TailorService {

    private final BaseResumeRepository baseResumeRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ml.tailor.url}")
    private String mlServiceUrl;

    @Value("${tailor.daily.limit}")
    private int dailyLimit;

    public TailorService(BaseResumeRepository baseResumeRepository, UserRepository userRepository,
                         RestTemplate restTemplate) {
        this.baseResumeRepository = baseResumeRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public TailorResponse tailor(String userEmail, UUID resumeId, TailorRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        checkAndUpdateCap(user);

        BaseResume resume = baseResumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have access to this resume");
        }

        Map<String, Object> mlRequest = new HashMap<>();
        Map<String, Object> resumeContext = new HashMap<>();

        try {
            if (resume.getCurrentDynamicFields() != null) {
                resumeContext = objectMapper.readValue(resume.getCurrentDynamicFields(), Map.class);
            }
        } catch (Exception ignored) {}
        mlRequest.put("resume_context", resumeContext);
        mlRequest.put("jd_text", request.getJdText());
        mlRequest.put("missing_keywords", request.getMissingKeywords());
        mlRequest.put("user_status", user.getStatus());

        Map<String, Object> mlResponse = restTemplate.postForObject(
                mlServiceUrl + "/api/tailor-resume", mlRequest, Map.class);

        String summary = (String) mlResponse.get("summary");
        List<String> skills = (List<String>) mlResponse.get("skills");
        List<List<String>> experienceBullets = (List<List<String>>) mlResponse.get("experienceBullets");

        int remaining = dailyLimit - user.getTailorCount();

        return new TailorResponse(summary, skills, experienceBullets, remaining);
    }

    private void checkAndUpdateCap(User user) {
        LocalDateTime now = LocalDateTime.now();

        if (user.getTailorWindowResetAt() == null || now.isAfter(user.getTailorWindowResetAt())) {
            user.setTailorCount(0);
            user.setTailorWindowResetAt(now.plusDays(1));
        }

        if (user.getTailorCount() >= dailyLimit) {
            throw new RuntimeException("Daily tailoring limit reached. Try again tomorrow.");
        }

        user.setTailorCount(user.getTailorCount() + 1);
        userRepository.save(user);
    }
}
