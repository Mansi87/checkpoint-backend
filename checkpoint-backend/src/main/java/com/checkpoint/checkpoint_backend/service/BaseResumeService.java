package com.checkpoint.checkpoint_backend.service;

import com.checkpoint.checkpoint_backend.dto.BaseResumeRequest;
import com.checkpoint.checkpoint_backend.dto.BaseResumeResponse;
import com.checkpoint.checkpoint_backend.model.BaseResume;
import com.checkpoint.checkpoint_backend.model.User;
import com.checkpoint.checkpoint_backend.repository.BaseResumeRepository;
import com.checkpoint.checkpoint_backend.repository.UserRepository;
import com.checkpoint.checkpoint_backend.security.RlsSessionHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BaseResumeService {

    private final BaseResumeRepository baseResumeRepository;
    private final UserRepository userRepository;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public BaseResumeService(BaseResumeRepository baseResumeRepository, UserRepository userRepository) {
        this.baseResumeRepository = baseResumeRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public BaseResumeResponse create(String userEmail, BaseResumeRequest request) {
        RlsSessionHelper.applyCurrentUser(entityManager);
        User user = getUserByEmail(userEmail);

        BaseResume resume = new BaseResume();
        resume.setUser(user);
        resume.setTitle(request.getTitle());
        resume.setTemplateId(request.getTemplateId());
        resume.setStaticFields(request.getStaticFields());
        resume.setCurrentDynamicFields(request.getCurrentDynamicFields());

        BaseResume saved = baseResumeRepository.save(resume);
        return toResponse(saved);
    }

    @Transactional
    public List<BaseResumeResponse> getAllForUser(String userEmail) {
        RlsSessionHelper.applyCurrentUser(entityManager);
        User user = getUserByEmail(userEmail);
        return baseResumeRepository.findByUserId(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public BaseResumeResponse getOne(String userEmail, UUID resumeId) {
        BaseResume resume = getOwnedResume(userEmail, resumeId);
        return toResponse(resume);
    }

    @Transactional
    public BaseResumeResponse update(String userEmail, UUID resumeId, BaseResumeRequest request) {
        BaseResume resume = getOwnedResume(userEmail, resumeId);

        resume.setTitle(request.getTitle());
        resume.setStaticFields(request.getStaticFields());
        resume.setCurrentDynamicFields(request.getCurrentDynamicFields());

        BaseResume saved = baseResumeRepository.save(resume);
        return toResponse(saved);
    }

    @Transactional
    public void delete(String userEmail, UUID resumeId) {
        BaseResume resume = getOwnedResume(userEmail, resumeId);
        baseResumeRepository.delete(resume);
    }

    // Authorization check lives here — reused by get/update/delete
    private BaseResume getOwnedResume(String userEmail, UUID resumeId) {
        RlsSessionHelper.applyCurrentUser(entityManager);
        User user = getUserByEmail(userEmail);
        BaseResume resume = baseResumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have access to this resume");
        }
        return resume;
    }

    private BaseResumeResponse toResponse(BaseResume r) {
        return new BaseResumeResponse(r.getId(), r.getTitle(), r.getTemplateId(),
                r.getStaticFields(), r.getCurrentDynamicFields(), r.getCreatedAt());
    }
}
