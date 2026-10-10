package com.carmod.app.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "Password must contain uppercase, lowercase, and a number")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @Pattern(regexp = "^[0-9+\\-\\s()]{7,15}$", message = "Please enter a valid phone number")
    private String phone;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
