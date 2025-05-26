package com.prpa.bancodigital.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class JwtToken extends AbstractAuthenticationToken {

    private final String token;
    private BankUser user;

    public JwtToken(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
    }

    public JwtToken(String token, BankUser user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        setUser(user);
    }

    public static Authentication authenticated(String token, BankUser user) {
        JwtToken jwtToken = new JwtToken(token, user, user.getAuthorities());
        jwtToken.setAuthenticated(true);
        return jwtToken;
    }

    public static Authentication unauthenticated(String token, BankUser user) {
        JwtToken jwtToken = new JwtToken(token, user, user.getAuthorities());
        jwtToken.setAuthenticated(false);
        return jwtToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }

    public String getToken() {
        return token;
    }

    public BankUser getUser() {
        return user;
    }

    public void setUser(BankUser user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JwtToken jwtToken = (JwtToken) o;
        return Objects.equals(token, jwtToken.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token);
    }
}
