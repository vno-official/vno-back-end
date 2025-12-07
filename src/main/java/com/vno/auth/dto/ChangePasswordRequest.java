package com.vno.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    public String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    public String newPassword;

    @NotBlank(message = "Password confirmation is required")
    public String confirmPassword;

    /**
     * Validate that new password and confirm password match
     */
    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }

    /**
     * Validate that new password is different from current password
     */
    public boolean passwordChanged() {
        return currentPassword != null && !currentPassword.equals(newPassword);
    }
}
