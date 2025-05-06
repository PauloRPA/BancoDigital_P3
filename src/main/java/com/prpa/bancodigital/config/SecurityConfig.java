package com.prpa.bancodigital.config;

import com.prpa.bancodigital.security.config.JwtAuthenticationProvider;
import com.prpa.bancodigital.security.config.JwtFilter;
import com.prpa.bancodigital.security.model.BankUser;
import com.prpa.bancodigital.security.model.Role;
import com.prpa.bancodigital.security.repository.BankUserRepository;
import com.prpa.bancodigital.security.service.BankUserService;
import com.prpa.bancodigital.security.service.JwtService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

import static com.prpa.bancodigital.config.ApplicationConfig.API_V1;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration
public class SecurityConfig {

    public static final String ACCESS_TOKEN_NAME = "access-token";
    public static final String REFRESH_TOKEN_NAME = "refresh-token";

    private static final String[] WHITE_LIST = {"/error/**", "/auth/**"};

    @Value("${application.default.user}")
    private String defaultUser;

    @Value("${application.default.password}")
    private String defaultPassword;

    @Value("${application.default.email}")
    private String defaultEmail;

    private final BankUserService bankUserService;
    private final JwtService jwtService;
    private final AuthenticationConfiguration authConfig;


    public SecurityConfig(BankUserService bankUserService, JwtService jwtService, AuthenticationConfiguration authConfig) {
        this.bankUserService = bankUserService;
        this.jwtService = jwtService;
        this.authConfig = authConfig;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        JwtFilter jwtFilter = new JwtFilter(antMatcher(API_V1 + "/**"), getAuthenticationManager(http), jwtService);
        return http
                .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(API_V1 + "/**").hasRole("ADMIN")
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(getDaoAuthenticationProvider())
                .authenticationProvider(getJwtAuthenticationProvider())
                .parentAuthenticationManager(authConfig.getAuthenticationManager())
                .eraseCredentials(true)
                .build();
    }

    public AuthenticationProvider getJwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtService);
    }

    public AuthenticationProvider getDaoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(bankUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    @Profile("pgdev || h2dev")
    public WebSecurityCustomizer ignoreH2() {
        return web -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**"))
                .requestMatchers(new AntPathRequestMatcher("/api-docs/**"));
    }

    @Bean
    public CommandLineRunner insertDefaultUserAndPassword(PasswordEncoder encoder, BankUserRepository bankUserRepository) {
        return args -> {
            if (bankUserRepository.findByUsername(defaultUser).isPresent())
                return;
            String message = """
                    GENERATED USERNAME AND PASSWORD:
                    USERNAME: %s
                    PASSWORD: %s
                    """.formatted(defaultUser, defaultPassword);

            bankUserRepository.save(new BankUser(null, defaultUser, defaultEmail, encoder.encode(defaultPassword), Arrays.stream(Role.values()).toList()));
            log.info(message);
        };
    }

    // OpenAPI + Swagger JWT authentication

    public SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()));
    }

}