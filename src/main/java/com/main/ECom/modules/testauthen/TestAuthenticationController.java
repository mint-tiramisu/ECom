package com.main.ECom.modules.testauthen;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.ECom.shared.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestAuthenticationController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        log.info("Public endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("This is a public endpoint", "No authentication required"));
    }

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<String>> protectedEndpoint(Authentication authentication) {
        String username = authentication.getName();
        log.info("Protected endpoint accessed by user: {}", username);
        return ResponseEntity.ok(ApiResponse.success("Protected endpoint accessed",
                "Hello " + username + ", you are authenticated"));
    }

    @GetMapping("/user-only")
    @PreAuthorize("hasAnyAuthority(T(com.main.ECom.auth.RoleConstants).USER, T(com.main.ECom.auth.RoleConstants).ADMIN, T(com.main.ECom.auth.RoleConstants).VENDOR)")
    public ResponseEntity<ApiResponse<String>> userOnlyEndpoint(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("UNKNOWN");
        log.info("User-only endpoint accessed by: {}", username);
        return ResponseEntity.ok(ApiResponse.success("User role confirmed",
                "You have USER role: " + username + " with role: " + role));
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority(T(com.main.ECom.auth.RoleConstants).ADMIN)")
    public ResponseEntity<ApiResponse<String>> adminOnlyEndpoint(Authentication authentication) {
        String username = authentication.getName();
        log.info("Admin-only endpoint accessed by: {}", username);
        return ResponseEntity.ok(ApiResponse.success("Admin role confirmed",
                "You have ADMIN role: " + username));
    }

    @GetMapping("/vendor-only")
    @PreAuthorize("hasAuthority(T(com.main.ECom.auth.RoleConstants).VENDOR)")
    public ResponseEntity<ApiResponse<String>> vendorOnlyEndpoint(Authentication authentication) {
        String username = authentication.getName();
        log.info("Vendor-only endpoint accessed by: {}", username);
        return ResponseEntity.ok(ApiResponse.success("Vendor role confirmed",
                "You have VENDOR role: " + username));
    }

    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<UserInfoResponse>> currentUserInfo(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("UNKNOWN");

        UserInfoResponse userInfo = new UserInfoResponse(username, role);
        log.info("Current user info requested for: {}", username);
        return ResponseEntity.ok(ApiResponse.success("User information retrieved", userInfo));
    }

    record UserInfoResponse(String email, String role) {
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(exception.getMessage()));
    }
}
