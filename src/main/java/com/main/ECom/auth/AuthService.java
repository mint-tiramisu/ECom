package com.main.ECom.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.ECom.auth.AuthDtos.LoginRequest;
import com.main.ECom.auth.AuthDtos.SignUpRequest;
import com.main.ECom.auth.AuthDtos.AuthResponse;
import com.main.ECom.auth.Role.RoleEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthResponse signUp(SignUpRequest signUpRequest) {
        log.info("Attempting to sign up user: {}", signUpRequest.email());
        String normalizedEmail = signUpRequest.email().trim().toLowerCase();
        if (authUserRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserDetailsImpl user = new UserDetailsImpl(normalizedEmail, passwordEncoder.encode(signUpRequest.password()), signUpRequest.name());
        
        Role userRole = roleRepository.findByName(RoleEnum.USER)
            .orElseGet(() -> roleRepository.save(new Role(RoleEnum.USER)));
        
        user.setRole(userRole);
        authUserRepository.save(user);
        log.info("User signed up: {}", normalizedEmail);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(normalizedEmail, signUpRequest.password())
        );
        String token = jwtUtils.generateJwtToken(authentication);

        return new AuthResponse("Signup successful", normalizedEmail, token);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        String normalizedEmail = loginRequest.email().trim().toLowerCase();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, loginRequest.password())
            );
            log.info("User logged in: {}", normalizedEmail);
            String token = jwtUtils.generateJwtToken(authentication);

            return new AuthResponse("Login successful", normalizedEmail, token);
        } catch (AuthenticationException ex) {
            log.warn("Login failed for: {}", normalizedEmail);
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}
