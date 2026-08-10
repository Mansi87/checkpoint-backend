package com.checkpoint.checkpoint_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class TailorRequest {

    @NotBlank
    private String jdText;

    private List<String> missingKeywords;
}
