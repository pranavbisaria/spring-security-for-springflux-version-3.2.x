package com.application.service;

import com.application.model.auth.request.LoginUserDto;
import com.application.model.auth.request.EmailDto;
import com.application.model.auth.request.RegenerateTokenDto;
import com.application.model.auth.response.AuthenticatedUser;
import com.application.model.auth.request.RegisterUserDto;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<Boolean> generateOtp(EmailDto emailDto);

    Mono<Boolean> existByEmail(String email);

    Mono<AuthenticatedUser> signUp(RegisterUserDto registerUserDto);

    Mono<AuthenticatedUser> login(LoginUserDto loginUserDto);

    Mono<AuthenticatedUser> regenerateAccessToken(@RequestBody RegenerateTokenDto tokenDto);
}
