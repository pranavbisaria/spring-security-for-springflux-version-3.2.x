package com.application.security;

import com.application.entity.User;
import com.application.exception.auth.UserNotAuthorizedException;
import com.application.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final JWTTokenService jwtTokenService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(BearerToken.class)
                .flatMap(auth -> {
                    Mono<User> user = this.jwtTokenService.getUserDetailsFromToken(auth.getCredentials());

                    return user.flatMap(u -> {
                        if (u.getUsername() == null) return Mono.error(new UserNotFoundException());
                        try {
                            if (this.jwtTokenService.validateAccessToken(u, auth.getCredentials())) {
                                return Mono.just(new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword(), u.getAuthorities()));
                            }
                        } catch (Exception e) {
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage()));
                        }
                        return Mono.error(new UserNotAuthorizedException());
                    });
                });
    }
}
