package com.wafrnalak.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
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
 * Default (production): stateless, CSRF disabled, deny-by-default.
 * Only explicitly listed public endpoints are accessible without authentication.
 *
 * Dev mode (security.insecure-test-mode=true via application-dev.yml):
 *   - All requests are permitted so the API is testable without a Firebase token.
 *   - Customer identity is provided via the X-Customer-Id request header.
 *   - NEVER enable this in any deployed environment.
 *
 * TODO (Firebase integration):
 *   1. Add a Firebase token verification filter before UsernamePasswordAuthenticationFilter.
 *   2. Remove DevCustomerAuthContext and implement CustomerAuthContext using Firebase claims.
 *   3. Remove X-Customer-Id header handling entirely.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${security.insecure-test-mode:false}")
    private boolean insecureTestMode;

    @Value("${app.security.public-docs-enabled:false}")
    private boolean publicDocsEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {

                if (publicDocsEnabled) {
                    // Dev convenience only.
                    auth.requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api-docs/**"
                    ).permitAll();
                }

                // ── Public health endpoints (for probes) ────────────────────
                auth.requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**"
                ).permitAll();

                // ── Always public: registration ────────────────────────────
                auth.requestMatchers(HttpMethod.POST, "/api/customers/register").permitAll();

                // ── Always public: read-only catalog endpoints ─────────────
                auth.requestMatchers(HttpMethod.GET,
                    "/api/categories",
                    "/api/categories/**",
                    "/api/products",
                    "/api/products/**",
                    "/api/reviews/product/**"
                ).permitAll();

                // ── Everything else ────────────────────────────────────────
                if (insecureTestMode) {
                    // DEV ONLY: permit all remaining requests for local testing.
                    // Gated behind security.insecure-test-mode=true (set only in application-dev.yml).
                    auth.anyRequest().permitAll();
                } else {
                    // PRODUCTION default: deny unless authenticated.
                    // TODO: wire Firebase token filter so this actually validates tokens.
                    auth.anyRequest().authenticated();
                }
            });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
