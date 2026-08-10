package com.checkpoint.checkpoint_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class JdAnalysisResponse {

    private UUID id;
    private Double baseScore;
    private List<String> missingKeywords;
}
