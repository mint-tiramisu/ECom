package com.main.ECom.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.ECom.auth.Role.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleEnum name);
}
