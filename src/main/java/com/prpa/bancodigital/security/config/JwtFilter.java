package com.prpa.bancodigital.security.config;

import com.prpa.bancodigital.security.model.BankUser;
import com.prpa.bancodigital.security.model.JwtToken;
import com.prpa.bancodigital.security.model.Role;
import com.prpa.bancodigital.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

public class JwtFilter extends AbstractAuthenticationProcessingFilter {

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
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        authHeader = isNull(authHeader) ? "" : authHeader;
        String token = "";
        BankUser user = new BankUser();

        try {
            if (!authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                Claims allClaims = jwtService.getAllClaims(token);
                user.setUsername(isNull(allClaims.getSubject()) ? "" : allClaims.getSubject());
                user.setEmail(isNull(allClaims.get("email")) ? "" : allClaims.get("email").toString());

                List<?> rolesClaim = allClaims.get("roles", List.class);
                rolesClaim = isNull(rolesClaim) || rolesClaim.isEmpty() ? List.of() : rolesClaim;
                List<Role> roles = rolesClaim.stream()
                        .map(obj -> Role.fromName(obj.toString()))
                        .filter(Objects::nonNull)
                        .toList();
                user.setRoles(roles);
            }
        } catch (ExpiredJwtException e) {
            throw new AccountExpiredException("Token expirado");
        } catch (JwtException e) {
            throw new AuthenticationServiceException("Não foi possível verificar o token");
        }

        return getAuthenticationManager().authenticate(JwtToken.unauthenticated(token, user));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

}
