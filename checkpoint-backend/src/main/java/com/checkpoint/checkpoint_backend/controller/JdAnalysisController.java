package com.checkpoint.checkpoint_backend.controller;


import com.checkpoint.checkpoint_backend.dto.JdAnalysisRequest;
import com.checkpoint.checkpoint_backend.dto.JdAnalysisResponse;
import com.checkpoint.checkpoint_backend.service.JdAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/resumes")
public class JdAnalysisController {

    private final JdAnalysisService jdAnalysisService;

    public JdAnalysisController(JdAnalysisService jdAnalysisService) {
        this.jdAnalysisService = jdAnalysisService;
    }

    @PostMapping("/{id}/analyze-jd")
    public ResponseEntity<JdAnalysisResponse> analyzeJd(Authentication auth, @PathVariable UUID id,
                                                        @Valid @RequestBody JdAnalysisRequest request) {
        return ResponseEntity.ok(jdAnalysisService.analyze(auth.getName(), id, request));
    }
}
