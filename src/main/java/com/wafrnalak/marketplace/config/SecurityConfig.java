package com.wafrnalak.marketplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration.
 *
 * Current state: stateless, CSRF disabled, public read-only endpoints permitted.
 * All write/mutating endpoints are explicitly listed here and currently permitted
 * so the API is testable before Firebase auth is wired in.
 *
 * TODO (Firebase integration):
 *   1. Add a Firebase token verification filter before UsernamePasswordAuthenticationFilter.
 *   2. Change the write-endpoint matchers below from .permitAll() to .authenticated().
 *   3. Remove customerId from @RequestParam in controllers — derive it from the verified token.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Swagger / OpenAPI (always public) ──────────────────────
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**"
                ).permitAll()

                // ── Auth (public) ───────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/customers/register").permitAll()

                // ── Public read-only endpoints ──────────────────────────────
                .requestMatchers(HttpMethod.GET,
                    "/api/categories",
                    "/api/categories/**",
                    "/api/products",
                    "/api/products/**",
                    "/api/reviews/product/**"
                ).permitAll()

                // ── Everything else: should require authentication ──────────
                // TODO: switch to .authenticated() once Firebase filter is in place
                .anyRequest().permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
