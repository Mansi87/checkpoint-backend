package com.checkpoint.checkpoint_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JdAnalysisRequest {

    @NotBlank
    private String jdText;
}
