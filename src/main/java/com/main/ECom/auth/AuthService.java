package com.main.ECom.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.ECom.auth.AuthDtos.LoginRequest;
import com.main.ECom.auth.AuthDtos.SignUpRequest;
import com.main.ECom.auth.AuthDtos.AuthResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse signUp(SignUpRequest signUpRequest) {
        log.info("Attempting to sign up user: {}", signUpRequest.email());
        String normalizedEmail = signUpRequest.email().trim().toLowerCase();
        if (authUserRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        AuthUser user = new AuthUser(normalizedEmail, passwordEncoder.encode(signUpRequest.password()));
        authUserRepository.save(user);
        log.info("User signed up: {}", normalizedEmail);

        return new AuthResponse("Signup successful", normalizedEmail);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        String normalizedEmail = loginRequest.email().trim().toLowerCase();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, loginRequest.password())
            );
            log.info("User logged in: {}", normalizedEmail);
        } catch (AuthenticationException ex) {
            log.warn("Login failed for: {}", normalizedEmail);
            throw new BadCredentialsException("Invalid email or password");
        }

        return new AuthResponse("Login successful", normalizedEmail);
    }
}
