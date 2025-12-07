package com.vno.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RequestPasswordResetRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    public String email;
}
