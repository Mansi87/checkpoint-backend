package com.checkpoint.checkpoint_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalaryLookupResponse {

    private Double minCtc;
    private Double maxCtc;
    private Double avgCtc;
    private Integer sampleSize;
}
