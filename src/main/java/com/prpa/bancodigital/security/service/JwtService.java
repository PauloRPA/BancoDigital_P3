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

    @Value("${application.security.expiration_sec.access_token}")
    public long ACCESS_TOKEN_EXPIRATION_SEC;

    @Value("${application.security.expiration_sec.refresh_token}")
    public long REFRESH_TOKEN_EXPIRATION_SEC;

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date exp = new Date((new Date().getTime()) + (REFRESH_TOKEN_EXPIRATION_SEC * 1000));
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateAccessToken(BankUser bankUser) {
        Date now = new Date();
        Date exp = new Date((new Date().getTime()) + (ACCESS_TOKEN_EXPIRATION_SEC * 1000));
        return Jwts.builder()
                .subject(bankUser.getUsername())
                .claim("email", bankUser.getEmail())
                .claim("roles", bankUser.getRoles())
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims getAllSignedClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getAllSignedClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public Boolean validateRefreshToken(String refreshToken, Claims accessTokenClaims) {
        if (isNull(refreshToken) || refreshToken.isBlank())
            return false;

        if (isNull(accessTokenClaims) || accessTokenClaims.isEmpty())
            return false;

        Claims refreshClaims = getAllSignedClaims(refreshToken);
        boolean valid = refreshClaims.getSubject().equals(accessTokenClaims.getSubject());
        return (valid) && !isTokenExpired(refreshToken);
    }

    public Boolean validateAccessToken(String token, BankUser bankUser) {
        if (isNull(token) || token.isBlank())
            return false;

        Claims claims = getAllSignedClaims(token);

        boolean valid = claims.getSubject().equals(bankUser.getUsername());
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
