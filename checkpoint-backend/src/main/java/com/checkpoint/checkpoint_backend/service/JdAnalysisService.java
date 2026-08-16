package com.checkpoint.checkpoint_backend.service;


import com.checkpoint.checkpoint_backend.dto.JdAnalysisRequest;
import com.checkpoint.checkpoint_backend.dto.JdAnalysisResponse;
import com.checkpoint.checkpoint_backend.model.BaseResume;
import com.checkpoint.checkpoint_backend.model.JdAnalysis;
import com.checkpoint.checkpoint_backend.model.User;
import com.checkpoint.checkpoint_backend.repository.BaseResumeRepository;
import com.checkpoint.checkpoint_backend.repository.JdAnalysisRepository;
import com.checkpoint.checkpoint_backend.repository.UserRepository;
import com.checkpoint.checkpoint_backend.security.RlsSessionHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JdAnalysisService {

    private final BaseResumeRepository baseResumeRepository;
    private final UserRepository userRepository;
    private final JdAnalysisRepository jdAnalysisRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    public JdAnalysisService(BaseResumeRepository baseResumeRepository, UserRepository userRepository,
                             JdAnalysisRepository jdAnalysisRepository, RestTemplate restTemplate) {
        this.baseResumeRepository = baseResumeRepository;
        this.userRepository = userRepository;
        this.jdAnalysisRepository = jdAnalysisRepository;
        this.restTemplate = restTemplate;
    }
    @Transactional
    public JdAnalysisResponse analyze(String userEmail, UUID resumeId, JdAnalysisRequest request) {
        RlsSessionHelper.applyCurrentUser(entityManager);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BaseResume resume = baseResumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have access to this resume");
        }

        String resumeText = (resume.getStaticFields() != null ? resume.getStaticFields() : "")
                + " " + (resume.getCurrentDynamicFields() != null ? resume.getCurrentDynamicFields() : "");

        Map<String, String> mlRequest = new HashMap<>();
        mlRequest.put("resume_text", resumeText);
        mlRequest.put("jd_text", request.getJdText());

        Map<String, Object> mlResponse = restTemplate.postForObject(
                mlServiceUrl + "/api/analyze-jd", mlRequest, Map.class);

        Double atsScore = ((Number) mlResponse.get("ats_score")).doubleValue();
        List<String> missingKeywords = (List<String>) mlResponse.get("missing_keywords");

        JdAnalysis analysis = new JdAnalysis();
        analysis.setBaseResume(resume);
        analysis.setJdText(request.getJdText());
        analysis.setBaseScore(atsScore);

        try {
            analysis.setMissingKeywords(objectMapper.writeValueAsString(missingKeywords));
        } catch (Exception e) {
            analysis.setMissingKeywords("[]");
        }

        JdAnalysis saved = jdAnalysisRepository.save(analysis);

        return new JdAnalysisResponse(saved.getId(), atsScore, missingKeywords);
    }
}
