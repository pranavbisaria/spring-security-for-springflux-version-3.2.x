package com.application.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(
                    exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)
                )
                .filter(s -> s.startsWith("Bearer "))
                .map(s -> new BearerToken(s.substring(7)));
    }
}
