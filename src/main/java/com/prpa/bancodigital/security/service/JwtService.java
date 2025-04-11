package com.prpa.bancodigital.security.service;

import com.prpa.bancodigital.security.model.BankUser;
import com.prpa.bancodigital.security.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Component
public class JwtService {

    @Value("${application.security.secret}")
    public String SECRET;

    @Value("${application.security.expiration_min}")
    public long JWT_EXPIRATION_MIN;

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJWToken(BankUser bankUser) {
        Date now = new Date();
        Date exp = new Date((new Date().getTime()) + (JWT_EXPIRATION_MIN * 60 * 1000));
        return Jwts.builder()
                .subject(bankUser.getUsername())
                .claim("email", bankUser.getEmail())
                .claim("roles", bankUser.getRoles())
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token, BankUser bankUser) {
        if (isNull(token) || token.isBlank())
            return false;

        Claims claims = getAllClaims(token);
        boolean valid = true;

        valid = valid && claims.getSubject().equals(bankUser.getUsername());
        valid = valid && claims.get("email").equals(bankUser.getEmail());
        List<?> claimRoles = claims.get("roles", List.class);
        List<Role> authorities = claimRoles.stream()
                .map(obj -> Role.fromName(obj.toString()))
                .filter(Objects::nonNull)
                .toList();
        valid = valid && new HashSet<>(bankUser.getRoles()).containsAll(authorities);
        return (valid) && !isTokenExpired(token);
    }

}
