package com.checkpoint.checkpoint_backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name= "resume_versions")
@Data
public class ResumeVersion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "base_resume_id", nullable = false)
    private BaseResume baseResume;

    @Column(columnDefinition = "TEXT")
    private String dynamicFieldsSnapshot;

    @Column(columnDefinition = "TEXT")
    private String jdText;

    private Double atsScore;

    private String label;

    private LocalDateTime createdAt = LocalDateTime.now();
}
