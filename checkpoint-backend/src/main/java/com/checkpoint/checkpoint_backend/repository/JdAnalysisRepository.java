package com.checkpoint.checkpoint_backend.repository;

import com.checkpoint.checkpoint_backend.model.JdAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JdAnalysisRepository extends JpaRepository<JdAnalysis, UUID> {


}
