package com.checkpoint.checkpoint_backend.repository;

import com.checkpoint.checkpoint_backend.model.BaseResume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BaseResumeRepository extends JpaRepository<BaseResume, UUID> {
    List<BaseResume> findByUserId(UUID userId);
}
