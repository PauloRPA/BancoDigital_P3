package com.prpa.bancodigital.security.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.security.model.BankUser;
import com.prpa.bancodigital.security.model.dtos.BankUserDTO;
import com.prpa.bancodigital.security.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.prpa.bancodigital.config.SecurityConfig.ACCESS_TOKEN_NAME;
import static com.prpa.bancodigital.config.SecurityConfig.REFRESH_TOKEN_NAME;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Value("${application.security.secure_cookies}")
    public Boolean SHOULD_COOKIES_BE_SECURE = true;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> postAuthenticate(@RequestBody BankUserDTO bankUserDTO, HttpServletResponse response) {
        Authentication authenticate = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(bankUserDTO.username(), bankUserDTO.password()));
        if (authenticate.isAuthenticated()) {
            String accessToken = jwtService.generateAccessToken((BankUser) authenticate.getPrincipal());
            String refreshToken = jwtService.generateRefreshToken(((BankUser) authenticate.getPrincipal()).getUsername());

            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_NAME, refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(SHOULD_COOKIES_BE_SECURE);
            refreshCookie.setPath(ApplicationConfig.API_V1);
            response.addCookie(refreshCookie);

            Cookie accessCookie = new Cookie(ACCESS_TOKEN_NAME, accessToken);
            accessCookie.setSecure(SHOULD_COOKIES_BE_SECURE);
            accessCookie.setPath(ApplicationConfig.API_V1);
            response.addCookie(accessCookie);

            return ResponseEntity.ok(Map.of("token", accessToken));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of());
    }

}