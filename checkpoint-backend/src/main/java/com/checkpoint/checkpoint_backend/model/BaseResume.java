package com.checkpoint.checkpoint_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="base_resumes")
@Data

public class BaseResume {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    private String templateId;

    @Column(columnDefinition = "TEXT")
    private String staticFields;

    @Column(columnDefinition = "TEXT")
    private String currentDynamicFields;

    private LocalDateTime createdAt = LocalDateTime.now();
}
