package com.main.ECom.auth;

import com.main.ECom.auth.Role.RoleEnum;

/**
 * Constants for use in @PreAuthorize and security expressions
 */
public final class RoleConstants {

    // For use in @PreAuthorize expressions: @PreAuthorize("hasAuthority(T(com.main.ECom.auth.RoleConstants).USER)")
    public static final String USER = RoleEnum.USER.name();
    public static final String VENDOR = RoleEnum.VENDOR.name();
    public static final String ADMIN = RoleEnum.ADMIN.name();

    private RoleConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
