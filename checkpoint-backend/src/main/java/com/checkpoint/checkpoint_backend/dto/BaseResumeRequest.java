package com.checkpoint.checkpoint_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BaseResumeRequest {

    @NotBlank
    private String title;

    private String templateId;

    private String staticFields;

    private String currentDynamicFields;
}
