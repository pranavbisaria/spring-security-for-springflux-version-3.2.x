package com.application.service.impl;

import com.application.entity.User;
import com.application.exception.auth.InvalidOTPException;
import com.application.exception.auth.InvalidPasswordException;
import com.application.exception.auth.UserAlreadyExistWithUserNameException;
import com.application.exception.user.UserNotFoundException;
import com.application.model.auth.request.LoginUserDto;
import com.application.model.auth.request.EmailDto;
import com.application.model.auth.request.RegenerateTokenDto;
import com.application.model.auth.response.AuthenticatedUser;
import com.application.model.auth.request.RegisterUserDto;
import com.application.repository.UserRepository;
import com.application.service.OTPService;
import com.application.service.UserService;
import com.application.security.JWTTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OTPService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenService jwtTokenService;

    @Override
    public Mono<Boolean> generateOtp(EmailDto emailDto) {
        return this.otpService.requestOTP(emailDto.email());
    }

    Mono<User> findByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<Boolean> existByEmail(String email) {
        return userRepository.findByEmail(email).map(User::getIsActive).defaultIfEmpty(Boolean.FALSE);
    }

    public Mono<Boolean> existByUserName(String email) {
        return userRepository.existsByUsername(email).defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Mono<AuthenticatedUser> signUp(RegisterUserDto registerUserDto) {
        return otpService.verifyOTP(registerUserDto.email(), registerUserDto.otp(), true)
                .flatMap(verified -> {
                    if (Boolean.TRUE.equals(verified)) {
                        return existByUserName(registerUserDto.userName())
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        return Mono.error(new UserAlreadyExistWithUserNameException());
                                    }
                                    return userRepository.save(new User(registerUserDto))
                                        .flatMap(this.jwtTokenService::generateAuthenticatedUserResponse);
                                });
                    }
                    return Mono.error(new InvalidOTPException());
                })
                .switchIfEmpty(Mono.error(new RuntimeException("OTP verification failed")));
    }

    @Override
    public Mono<AuthenticatedUser> login(@RequestBody LoginUserDto loginUserDto) {
        return findByEmail(loginUserDto.email())
                .flatMap(user -> {
                    if (!this.passwordEncoder.matches(loginUserDto.password(), user.getPassword())) {
                        return Mono.error(new InvalidPasswordException());
                    }
                    return this.jwtTokenService.generateAuthenticatedUserResponse(user);
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    @Override
    public Mono<AuthenticatedUser> regenerateAccessToken(@RequestBody RegenerateTokenDto tokenDto) {
        return this.jwtTokenService.regenerateAccessToken(tokenDto.refreshToken());
    }
}
