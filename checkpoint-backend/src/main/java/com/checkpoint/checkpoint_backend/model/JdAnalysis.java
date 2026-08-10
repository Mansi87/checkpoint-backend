package com.checkpoint.checkpoint_backend.model;


import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jd_analyses")
@Data
public class JdAnalysis {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "base_resume_id", nullable = false)
    private BaseResume baseResume;

    @Column(columnDefinition = "TEXT")
    private String jdText;

    private Double baseScore;

    @Column(columnDefinition = "TEXT")
    private String missingKeywords; // stored as JSON string array

    private Double tailoredScore;

    private LocalDateTime createdAt = LocalDateTime.now();
}
