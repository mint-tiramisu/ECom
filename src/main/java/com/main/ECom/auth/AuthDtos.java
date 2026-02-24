package com.main.ECom.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

	public record SignUpRequest(
			@NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
			@NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password,
			@NotBlank(message = "Name is required") String name) {
	}

	public record LoginRequest(
			@NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
			@NotBlank(message = "Password is required") String password) {
	}

	public record AuthResponse(String message, String email, String token) {
	}

	private AuthDtos() {
	}
}
