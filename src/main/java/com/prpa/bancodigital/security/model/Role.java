package com.prpa.bancodigital.security.model;

public enum Role {

    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static Role fromName(String name) {
        for (Role value : values()) {
            if (name.equals(value.name()))
                return value;
        }
        return null;
    }

}
