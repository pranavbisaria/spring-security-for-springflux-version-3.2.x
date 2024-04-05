package com.application.config;

import com.application.security.AuthConverter;
import com.application.security.AuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final PropertiesConfig propertiesConfig;
    private final String[] PUBLIC_URLs = {
            "/api/public/user/**", "/",
            "/v3/api-docs/**", "/webjars/**", "/docs"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, AuthConverter authConverter, AuthenticationManager authenticationManager) {
        AuthenticationWebFilter jwtAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        jwtAuthenticationFilter.setServerAuthenticationConverter(authConverter);

        return http.exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                            .authenticationEntryPoint((exchange, e) ->
                                    Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                            ).accessDeniedHandler((exchange, e) ->
                                    Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                            )
                    )
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                    .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                    .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                    .authorizeExchange(auth -> auth
                            .pathMatchers(PUBLIC_URLs).permitAll()
                            .anyExchange().authenticated()
                    ).build();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(propertiesConfig.getCors().getAllowedOrigins());
        corsConfig.setMaxAge(8000L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }
}
