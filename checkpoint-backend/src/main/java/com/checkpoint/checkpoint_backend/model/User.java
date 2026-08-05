package com.checkpoint.checkpoint_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data

public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    private String googleId;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime deletedAt;

    private Integer tailorCount = 0;

    private LocalDateTime tailorWindowResetAt;

    private String tier = "free";

    private String salaryPromptStatus = "not_shown";

    private String firstName;
    private String lastName;
    private String status;
}
