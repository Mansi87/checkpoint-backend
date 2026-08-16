package com.checkpoint.checkpoint_backend.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ParsedResumeResponse {

    private Map<String, String> extractedFields;
    private Map<String, Boolean> confidence;
    private List<String> completenessFlags;
    private Map<String, List<String>> sections;
}
