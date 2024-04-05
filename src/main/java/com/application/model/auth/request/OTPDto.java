package com.application.model.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OTPDto (
        @Email(message = "Email address is not valid")
        String email,
        @NotBlank(message = "OTP can not be empty")
        @Size(min = 6, max = 6, message = "OTP must be 6 characters long")
        @Pattern(regexp = "\\d{6}", message = "OTP must contain only digits")
        String otp
) {}