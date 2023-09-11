package com.example.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration //TODO:1
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityService {
    private  final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationConfig authenticationConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return http
                .csrf()
                .disable()
                .authenticationProvider(authenticationConfig.authenticationProvider())
                .addFilter(new UsernamePasswordAuthenticationFilter(authenticationConfig.authenticationManager(new AuthenticationConfiguration())))
                .authorizeHttpRequests()
                .requestMatchers("/register","/api/v1/auth/**",
                                "/api/v1/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                        ).permitAll()
                .requestMatchers("/login").hasAuthority("ADMIN")
                .requestMatchers("/login").hasAuthority("CUSTOMER")
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
               .oauth2Login().and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
