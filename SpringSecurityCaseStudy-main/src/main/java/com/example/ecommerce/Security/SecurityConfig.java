package com.example.ecommerce.Security;

import com.example.ecommerce.model.LoginProvider;
import com.example.ecommerce.model.SecuredUser;
import com.example.ecommerce.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.UUID;


@Configuration //TODO:1
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
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
                .requestMatchers("/api/v1/auth/register","/api/v1/auth/login",
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
               .and().authorizeHttpRequests()
               .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().formLogin(Customizer.withDefaults())
               .oauth2Login(oc -> oc.userInfoEndpoint(ui -> ui.userService(oauth2LoginHandler()).oidcUserService(oidcLoginHandler())))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcLoginHandler() {
        return userRequest -> {
            LoginProvider provider = LoginProvider.valueOf(userRequest.getClientRegistration().getClientId().toUpperCase());
            OidcUserService delegate = new OidcUserService();
            OidcUser oidcUser = delegate.loadUser(userRequest);
            return SecuredUser.builder().user(User.builder().firstName(oidcUser.getName())
                            .email(oidcUser.getEmail())
                    .password(UUID.randomUUID().toString()).loginProvider(provider)
                    .build()).build();
        };
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2LoginHandler() {
        return userRequest -> {
            LoginProvider provider = LoginProvider.valueOf(userRequest.getClientRegistration().getClientId().toUpperCase());
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            return SecuredUser.builder().user(User.builder().firstName(oAuth2User.getName())
                    .password(UUID.randomUUID().toString()).loginProvider(provider)
                    .build()).build();
        };
    }

}
