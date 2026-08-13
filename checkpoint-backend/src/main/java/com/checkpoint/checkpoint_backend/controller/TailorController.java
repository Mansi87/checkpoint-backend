package com.checkpoint.checkpoint_backend.controller;


import com.checkpoint.checkpoint_backend.dto.SaveVersionRequest;
import com.checkpoint.checkpoint_backend.dto.TailorRequest;
import com.checkpoint.checkpoint_backend.dto.TailorResponse;
import com.checkpoint.checkpoint_backend.service.ResumeVersionService;
import com.checkpoint.checkpoint_backend.service.TailorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/resumes")
public class TailorController {

    private final TailorService tailorService;
    private final ResumeVersionService resumeVersionService;

    public TailorController(TailorService tailorService, ResumeVersionService resumeVersionService) {
        this.tailorService = tailorService;
        this.resumeVersionService = resumeVersionService;
    }

    @PostMapping("/{id}/tailor")
    public ResponseEntity<?> tailor(Authentication auth, @PathVariable UUID id,
                                    @Valid @RequestBody TailorRequest request) {
        try {
            return ResponseEntity.ok(tailorService.tailor(auth.getName(), id, request));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("limit reached")) {
                return ResponseEntity.status(429).body(e.getMessage());
            }
            throw e;
        }
    }

    @PostMapping("/{id}/versions")
    public ResponseEntity<Map<String, Object>> saveVersion(Authentication auth, @PathVariable UUID id,
                                                           @RequestBody SaveVersionRequest request) {
        return ResponseEntity.ok(resumeVersionService.saveVersion(auth.getName(), id, request));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<Map<String, Object>>> getVersions(Authentication auth, @PathVariable UUID id) {
        return ResponseEntity.ok(resumeVersionService.getVersionHistory(auth.getName(), id));
    }
}
