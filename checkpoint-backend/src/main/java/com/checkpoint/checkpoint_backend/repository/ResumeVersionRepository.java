package com.checkpoint.checkpoint_backend.repository;

import com.checkpoint.checkpoint_backend.model.ResumeVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, UUID> {

    List<ResumeVersion> findByBaseResumeIdOrderByCreatedAtDesc(UUID baseResumeId);
}
