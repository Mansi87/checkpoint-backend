package com.checkpoint.checkpoint_backend.service;

import com.checkpoint.checkpoint_backend.dto.SalaryLookupResponse;
import com.checkpoint.checkpoint_backend.dto.SalarySubmitRequest;
import com.checkpoint.checkpoint_backend.model.SalaryData;
import com.checkpoint.checkpoint_backend.model.User;
import com.checkpoint.checkpoint_backend.repository.SalaryDataRepository;
import com.checkpoint.checkpoint_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryService {

    private final SalaryDataRepository salaryDataRepository;
    private final UserRepository userRepository;

    public SalaryService(SalaryDataRepository salaryDataRepository, UserRepository userRepository) {
        this.salaryDataRepository = salaryDataRepository;
        this.userRepository = userRepository;
    }

    public SalaryLookupResponse lookup(String role, String city) {
        List<SalaryData> matches = (city != null && !city.isBlank())
                ? salaryDataRepository.findByRoleIgnoreCaseAndCityIgnoreCase(role, city)
                : salaryDataRepository.findByRoleIgnoreCase(role);

        if (matches.isEmpty()) {
            return new SalaryLookupResponse(null, null, null, 0);
        }

        double min = matches.stream().mapToDouble(SalaryData::getCtcLpa).min().orElse(0);
        double max = matches.stream().mapToDouble(SalaryData::getCtcLpa).max().orElse(0);
        double avg = matches.stream().mapToDouble(SalaryData::getCtcLpa).average().orElse(0);

        return new SalaryLookupResponse(min, max, Math.round(avg * 10) / 10.0, matches.size());
    }

    public void submit(String userEmail, SalarySubmitRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SalaryData entry = new SalaryData();
        entry.setRole(request.getRole());
        entry.setExperienceLevel(request.getExperienceLevel());
        entry.setExperienceBand(request.getExperienceBand());
        entry.setCity(request.getCity());
        entry.setCtcLpa(request.getCtcLpa());
        entry.setSource("crowdsourced");
        entry.setUserId(user.getId());

        salaryDataRepository.save(entry);

        user.setSalaryPromptStatus("submitted");
        userRepository.save(user);
    }

    public void skip(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setSalaryPromptStatus("skipped");
        userRepository.save(user);
    }

    public boolean shouldShowPrompt(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"not_shown".equals(user.getSalaryPromptStatus())) return false;

        return user.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(60));
    }
}
