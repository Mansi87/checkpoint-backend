package com.checkpoint.checkpoint_backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="salary_data")
@Data
public class SalaryData {

    @Id
    @GeneratedValue
    private UUID id;

    private String role;
    private String experienceLevel; // "fresher" or "experienced"
    private String experienceBand;
    private String city;
    private Double ctcLpa;
    private String source; // "seed" or "crowdsourced"

    private UUID userId; // internal only, never exposed in responses

    private LocalDateTime createdAt = LocalDateTime.now();
}
