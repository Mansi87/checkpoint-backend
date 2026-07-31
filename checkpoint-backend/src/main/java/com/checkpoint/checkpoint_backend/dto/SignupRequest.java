package com.checkpoint.checkpoint_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
