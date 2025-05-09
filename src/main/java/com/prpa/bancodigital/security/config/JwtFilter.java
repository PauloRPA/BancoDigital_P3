package com.prpa.bancodigital.security.config;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.security.model.BankUser;
import com.prpa.bancodigital.security.model.JwtToken;
import com.prpa.bancodigital.security.model.Role;
import com.prpa.bancodigital.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.prpa.bancodigital.config.SecurityConfig.ACCESS_TOKEN_NAME;
import static com.prpa.bancodigital.config.SecurityConfig.REFRESH_TOKEN_NAME;
import static java.util.Objects.isNull;

public class JwtFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${application.security.secure_cookies}")
    public boolean shouldCookiesBeSecure = true;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtService jwtService;

    public JwtFilter(AntPathRequestMatcher requestMatcher, AuthenticationManager authenticationManager, JwtService jwtService) {
        super(requestMatcher, authenticationManager);
        this.jwtService = jwtService;
        setAuthenticationSuccessHandler((req, res, auth) -> {
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String token = "";
        Claims tokenClaims = null;

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        authHeader = isNull(authHeader) ? "" : authHeader;
        if (!authHeader.isBlank() && authHeader.startsWith("Bearer "))
            token = authHeader.substring(7);
        else
            throw new BadCredentialsException("Não foi possível autenticar usuário devido a ausência do token");

        try {
            tokenClaims = jwtService.getAllSignedClaims(token);
        } catch (ExpiredJwtException e) {
            token = handleExpiredToken(request, response, e.getClaims());
        } catch (JwtException e) {
            throw new AuthenticationServiceException("Não foi possível verificar o token");
        }

        if (isNull(tokenClaims)) {
            try {
                tokenClaims = jwtService.getAllSignedClaims(token);
            } catch (JwtException e) {
                throw new AuthenticationServiceException("Não foi possível verificar o token");
            }
        }

        return getAuthenticationManager().authenticate(JwtToken.unauthenticated(token, mapClaimFieldsToUser(tokenClaims)));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    private String handleExpiredToken(HttpServletRequest request, HttpServletResponse response, Claims expiredTokenClaims) {
        String newAccessToken = getNewAccessFromRefreshToken(request, expiredTokenClaims)
                .orElseThrow(() -> new AccountExpiredException("Refresh Token expirado"));
        Cookie cookie = new Cookie(ACCESS_TOKEN_NAME, newAccessToken);
        cookie.setSecure(shouldCookiesBeSecure);
        cookie.setPath(ApplicationConfig.API_V1);
        response.addCookie(cookie);
        return newAccessToken;
    }

    private Optional<String> getNewAccessFromRefreshToken(HttpServletRequest request, Claims expiredAccessTokenClaims) {
        String refreshCookie = Optional.ofNullable(request.getCookies())
                .map(Arrays::stream)
                .flatMap(cookieStream ->
                        cookieStream.filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_NAME))
                                .findFirst())
                .map(Cookie::getValue)
                .orElse("");
        if (!jwtService.validateRefreshToken(refreshCookie, expiredAccessTokenClaims))
            return Optional.empty();

        return Optional.of(jwtService.generateAccessToken(mapClaimFieldsToUser(expiredAccessTokenClaims)));
    }

    private static BankUser mapClaimFieldsToUser(Claims allClaims) {
        BankUser user = new BankUser();
        user.setUsername(isNull(allClaims.getSubject()) ? "" : allClaims.getSubject());
        user.setEmail(isNull(allClaims.get("email")) ? "" : allClaims.get("email").toString());

        List<?> rolesClaim = allClaims.get("roles", List.class);
        rolesClaim = isNull(rolesClaim) || rolesClaim.isEmpty() ? List.of() : rolesClaim;
        List<Role> roles = rolesClaim.stream()
                .map(obj -> Role.fromName(obj.toString()))
                .filter(Objects::nonNull)
                .toList();
        user.setRoles(roles);
        return user;
    }

}