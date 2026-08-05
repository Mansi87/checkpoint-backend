package com.checkpoint.checkpoint_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BaseResumeResponse {

    private UUID id;
    private String title;
    private String templateId;
    private String staticFields;
    private String currentDynamicFields;
    private LocalDateTime createdAt;
}
