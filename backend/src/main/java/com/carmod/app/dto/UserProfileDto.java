package com.carmod.app.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileDto {

    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Pattern(regexp = "^[0-9+\\-\\s()]{7,15}$", message = "Please enter a valid phone number")
    private String phone;

    // Optional password change
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    private String confirmNewPassword;

    public boolean isPasswordChangeRequested() {
        return newPassword != null && !newPassword.isBlank();
    }

    public boolean isPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }
}
