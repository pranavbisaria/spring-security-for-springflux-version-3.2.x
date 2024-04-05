package com.application.security;

import com.application.config.PropertiesConfig;
import com.application.entity.User;
import com.application.exception.auth.InvalidRefreshTokenException;
import com.application.model.auth.response.AuthenticatedUser;
import com.application.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import static com.application.security.token.TokenType.*;


@Service
@RequiredArgsConstructor
public class JWTTokenService {
    private final UserRepository userRepository;
    private final PropertiesConfig propertiesConfig;

    private static final String ROLE_TAG = "role";
    private static final String TOKEN_TYPE_TAG = "t-type";

    private String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + propertiesConfig.getJwt().getValidity().getRefreshTokenValidity()))
                .issuer(propertiesConfig.getJwt().getIssuer())
                .claim(ROLE_TAG, user.getAuthorities().stream().map(Object::toString).toList())
                .claim(TOKEN_TYPE_TAG, ACCESS_TOKEN.toString())
                .signWith(getSignKey()).compact();
    }

    private String generateRefreshToken(User user){
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + propertiesConfig.getJwt().getValidity().getRefreshTokenValidity()))
                .issuer(propertiesConfig.getJwt().getIssuer())
                .claim(ROLE_TAG, user.getAuthorities().stream().map(Object::toString).toList())
                .claim(TOKEN_TYPE_TAG, REFRESH_TOKEN.toString())
                .signWith(getSignKey()).compact();
    }

    public Mono<AuthenticatedUser> generateAuthenticatedUserResponse(User user){
        return Mono.just(new AuthenticatedUser(
                generateAccessToken(user),
                generateRefreshToken(user),
                user.getUsername()));
    }

    public Mono<AuthenticatedUser> regenerateAccessToken(String token) {
        return getUserDetailsFromToken(token)
                .flatMap(user -> {
                    if (!validateRefreshToken(user, token)) {
                        return Mono.error(new InvalidRefreshTokenException());
                    }
                    return Mono.just(new AuthenticatedUser(generateAccessToken(user), token, user.getUsername()));
                })
                .switchIfEmpty(Mono.error(new InvalidRefreshTokenException()))
                .onErrorMap(JwtException.class, ex -> new InvalidRefreshTokenException());
    }

    private SecretKey getSignKey() {
        var keyBytes= Decoders.BASE64.decode(propertiesConfig.getJwt().getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getClaims(String token){
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid token");
        }
    }

    public boolean validateAccessToken(UserDetails user, String token) {
        var claims = getClaims(token);
        return !claims.getExpiration().before(new Date(System.currentTimeMillis()))
                && claims.getIssuer().equals(propertiesConfig.getJwt().getIssuer())
                && claims.get(TOKEN_TYPE_TAG).equals(ACCESS_TOKEN.toString())
                && claims.getSubject().equals(user.getUsername());
    }

    public boolean validateRefreshToken(UserDetails user, String token) {
        var claims = getClaims(token);
        return !claims.getExpiration().before(new Date(System.currentTimeMillis()))
                && claims.getIssuer().equals(propertiesConfig.getJwt().getIssuer())
                && claims.get(TOKEN_TYPE_TAG).equals(REFRESH_TOKEN.toString())
                && claims.getSubject().equals(user.getUsername());
    }

    public Mono<User> getUserDetailsFromToken(String token) {
        return this.userRepository.findByEmail(getUserNameFromToken(token));
    }

    public String getUserNameFromToken(String token) {
        return getClaims(token).getSubject();
    }
}
