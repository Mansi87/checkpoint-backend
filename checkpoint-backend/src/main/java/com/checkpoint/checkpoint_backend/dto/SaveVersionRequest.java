package com.checkpoint.checkpoint_backend.dto;

import lombok.Data;

@Data
public class SaveVersionRequest {

    private String summary;
    private String skills; // JSON string
    private String experienceBullets; // JSON string
    private String jdText;
    private Double atsScore;
    private String label;
}
