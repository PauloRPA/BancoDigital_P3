package com.prpa.bancodigital.security.model;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static Role fromName(String name) {
        for (Role value : values()) {
            if (name.equals(value.name()))
                return value;
        }
        return null;
    }

}
