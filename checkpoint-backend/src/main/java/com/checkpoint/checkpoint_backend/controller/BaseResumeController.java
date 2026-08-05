package com.checkpoint.checkpoint_backend.controller;


import com.checkpoint.checkpoint_backend.dto.BaseResumeRequest;
import com.checkpoint.checkpoint_backend.dto.BaseResumeResponse;
import com.checkpoint.checkpoint_backend.service.BaseResumeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/resumes")
public class BaseResumeController {

    private final BaseResumeService baseResumeService;

    public BaseResumeController(BaseResumeService baseResumeService) {
        this.baseResumeService = baseResumeService;
    }

    @PostMapping
    public ResponseEntity<BaseResumeResponse> create(Authentication auth, @Valid @RequestBody BaseResumeRequest request) {
        return ResponseEntity.ok(baseResumeService.create(auth.getName(), request));
    }

    @GetMapping
    public ResponseEntity<List<BaseResumeResponse>> getAll(Authentication auth) {
        return ResponseEntity.ok(baseResumeService.getAllForUser(auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResumeResponse> getOne(Authentication auth, @PathVariable UUID id) {
        return ResponseEntity.ok(baseResumeService.getOne(auth.getName(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResumeResponse> update(Authentication auth, @PathVariable UUID id, @Valid @RequestBody BaseResumeRequest request) {
        return ResponseEntity.ok(baseResumeService.update(auth.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable UUID id) {
        baseResumeService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
