package com.checkpoint.checkpoint_backend.service;

import com.checkpoint.checkpoint_backend.dto.ParsedResumeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ParseService {

    private final RestTemplate restTemplate;

    @Value("${ml.parse.url}")
    private String mlServiceUrl;

    public ParseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ParsedResumeResponse parseAndCheck(MultipartFile file) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        Map<String, Object> mlResponse = restTemplate.postForObject(
                mlServiceUrl + "/api/parse-resume", requestEntity, Map.class);

        ParsedResumeResponse response = new ParsedResumeResponse();
        response.setExtractedFields((Map<String, String>) mlResponse.get("extractedFields"));
        response.setConfidence((Map<String, Boolean>) mlResponse.get("confidence"));
        response.setCompletenessFlags(checkCompleteness(response.getExtractedFields(), response.getConfidence()));

        return response;
    }

    // Completeness check — flags gaps, never auto-fills. Same principle as the tailoring prompt: advise, don't fabricate.
    private List<String> checkCompleteness(Map<String, String> fields, Map<String, Boolean> confidence) {
        List<String> flags = new ArrayList<>();

        if (confidence == null || !Boolean.TRUE.equals(confidence.get("email"))) {
            flags.add("No email address detected — add it manually.");
        }
        if (confidence == null || !Boolean.TRUE.equals(confidence.get("phone"))) {
            flags.add("No phone number detected — add it manually.");
        }
        if (confidence == null || !Boolean.TRUE.equals(confidence.get("linkedin"))) {
            flags.add("No LinkedIn URL found — consider adding one.");
        }
        if (fields == null || fields.get("fullName") == null || fields.get("fullName").isBlank()) {
            flags.add("Name could not be confidently detected — please confirm.");
        }

        return flags;
    }
}
