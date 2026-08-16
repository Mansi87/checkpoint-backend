package com.checkpoint.checkpoint_backend.repository;

import com.checkpoint.checkpoint_backend.model.SalaryData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalaryDataRepository extends JpaRepository<SalaryData, UUID> {

    List<SalaryData> findByRoleIgnoreCaseAndCityIgnoreCase(String role, String city);
    List<SalaryData> findByRoleIgnoreCase(String role);
}
