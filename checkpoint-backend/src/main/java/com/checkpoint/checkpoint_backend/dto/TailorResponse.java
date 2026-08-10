package com.checkpoint.checkpoint_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TailorResponse {

    private String summary;
    private List<String> skills;
    private List<List<String>> experienceBullets;
    private Integer remainingTailors;
}
