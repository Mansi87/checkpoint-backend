package com.checkpoint.checkpoint_backend.controller;


import com.checkpoint.checkpoint_backend.service.ParseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
public class ParseController {

    private final ParseService parseService;

    public ParseController(ParseService parseService) {
        this.parseService = parseService;
    }

    @PostMapping("/parse-upload")
    public ResponseEntity<?> parseUpload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(parseService.parseAndCheck(file));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to parse resume: " + e.getMessage());
        }
    }
}
