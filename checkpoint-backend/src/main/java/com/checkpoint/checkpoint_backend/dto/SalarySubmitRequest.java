package com.checkpoint.checkpoint_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalarySubmitRequest {

    @NotBlank
    private String role;
    @NotBlank
    private String experienceLevel;
    private String experienceBand;
    @NotBlank
    private String city;
    @NotNull
    private Double ctcLpa;

}
