package com.checkpoint.checkpoint_backend.controller;

import com.checkpoint.checkpoint_backend.dto.SalaryLookupResponse;
import com.checkpoint.checkpoint_backend.dto.SalarySubmitRequest;
import com.checkpoint.checkpoint_backend.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping("/lookup")
    public ResponseEntity<SalaryLookupResponse> lookup(@RequestParam String role, @RequestParam(required = false) String city) {
        return ResponseEntity.ok(salaryService.lookup(role, city));
    }

    @PostMapping("/submit")
    public ResponseEntity<Void> submit(Authentication auth, @Valid @RequestBody SalarySubmitRequest request) {
        salaryService.submit(auth.getName(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/skip")
    public ResponseEntity<Void> skip(Authentication auth) {
        salaryService.skip(auth.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/should-prompt")
    public ResponseEntity<Boolean> shouldPrompt(Authentication auth) {
        return ResponseEntity.ok(salaryService.shouldShowPrompt(auth.getName()));
    }
}
