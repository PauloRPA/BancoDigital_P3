package com.prpa.bancodigital.security.controller;

import com.prpa.bancodigital.security.model.BankUser;
import com.prpa.bancodigital.security.model.dtos.BankUserDTO;
import com.prpa.bancodigital.security.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

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
            String generatedToken = jwtService.generateJWToken((BankUser) authenticate.getPrincipal());
            response.addCookie(new Cookie("access-token", generatedToken));
            return ResponseEntity.ok(Map.of("token", generatedToken));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of());
    }

}
