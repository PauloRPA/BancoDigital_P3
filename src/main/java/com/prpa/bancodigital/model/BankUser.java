package com.prpa.bancodigital.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankUser implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private List<Role> roles;

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isNull(roles))
            return List.of();
        return roles.stream().map(Role::name).map(SimpleGrantedAuthority::new).toList();
    }

}
