package com.cst438.project02.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Filter Chain to allow anyone to access to the Google Auth route.
        return (SecurityFilterChain) http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/**", "/error").permitAll();
                    auth.requestMatchers("/error").permitAll();
                    auth.requestMatchers("/h2-console/**").permitAll();
                    auth.requestMatchers("https://developers.google.com/oauthplayground/*").permitAll();
                    auth.anyRequest().authenticated();
                })
                // Since our application is stateless, we wouldn't need CSRF protection
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(req -> {
                    var c = new org.springframework.web.cors.CorsConfiguration();
                    c.setAllowedOrigins(java.util.List.of(System.getenv().getOrDefault("CORS_ORIGIN", "*")));
                    c.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
                    c.setAllowedHeaders(java.util.List.of("*"));
                    c.setAllowCredentials(true);
                    return c;
                }))
                .headers(headers -> headers
                        .frameOptions(Customizer.withDefaults()).disable()
                )
                .build();
    }

}
