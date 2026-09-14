package com.checkpoint.checkpoint_backend.repository;

import com.checkpoint.checkpoint_backend.model.JdAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JdAnalysisRepository extends JpaRepository<JdAnalysis, UUID> {
    java.util.List<com.checkpoint.checkpoint_backend.model.JdAnalysis> findTop5ByBaseResume_User_IdOrderByCreatedAtDesc(java.util.UUID userId);

}
