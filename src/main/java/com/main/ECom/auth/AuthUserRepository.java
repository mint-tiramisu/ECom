package com.main.ECom.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserRepository extends JpaRepository<UserDetailsImpl, Long> {

    Optional<UserDetailsImpl> findByEmail(String email);

    boolean existsByEmail(String email);
}
