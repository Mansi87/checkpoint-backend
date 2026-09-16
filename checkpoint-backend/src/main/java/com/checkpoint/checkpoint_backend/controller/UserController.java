package com.checkpoint.checkpoint_backend.controller;


import com.checkpoint.checkpoint_backend.dto.UserResponse;
import com.checkpoint.checkpoint_backend.model.User;
import com.checkpoint.checkpoint_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(new UserResponse(user.getFirstName(), user.getLastName(), user.getEmail(), user.getTier(), user.getDigestFrequency()));
    }

    @PutMapping("/digest-frequency")
    public ResponseEntity<Void> updateDigestFrequency(Authentication auth, @RequestBody Map<String, String> body) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDigestFrequency(body.get("frequency"));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
