package com.prpa.bancodigital.security.config;

import com.prpa.bancodigital.security.model.JwtToken;
import com.prpa.bancodigital.security.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;

    public JwtAuthenticationProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof JwtToken jwt))
            throw new AuthenticationServiceException("Apenas são suportados JwtTokens");

        try {
            if (jwtService.validateToken(jwt.getToken(), jwt.getUser())) {
                return JwtToken.authenticated(jwt.getToken(), jwt.getUser());
            }
        } catch (ExpiredJwtException e) {
            throw new AccountExpiredException("Token expirado");
        } catch (JwtException e) {
            throw new AuthenticationServiceException("Não foi possível validar token");
        }
        throw new BadCredentialsException("Não foi possível validar o token");
    }

}
