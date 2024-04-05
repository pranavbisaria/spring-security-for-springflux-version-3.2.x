package com.application.controller;

import com.application.model.ResponseDto;
import com.application.model.auth.request.*;
import com.application.model.auth.response.AuthenticatedUser;
import com.application.service.OTPService;
import com.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/public/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OTPService otpService;

    @PostMapping("/get-otp")
    public Mono<ResponseEntity<ResponseDto>> getOtp(@Valid @RequestBody EmailDto emailDto) {
        return this.userService.generateOtp(emailDto).thenReturn(
                    ResponseEntity.ok(new ResponseDto("OTP successfully sent!", HttpStatus.OK)));
    }

    @PostMapping("/verify-otp")
    public Mono<ResponseEntity<ResponseDto>> verifyOtp(@Valid @RequestBody OTPDto otpDto) {
        return this.otpService.verifyOTP(otpDto.email(), otpDto.otp(), false).thenReturn(
                        ResponseEntity.ok(new ResponseDto("OTP successfully verified!", HttpStatus.OK)));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthenticatedUser>> signUp(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return this.userService.signUp(registerUserDto).map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthenticatedUser>> login(@Valid @RequestBody LoginUserDto loginUserDto) {
        return this.userService.login(loginUserDto).map(ResponseEntity::ok);
    }

    @PostMapping("/regenerate-access-token")
    public Mono<ResponseEntity<AuthenticatedUser>> regenerateAccessToken(@Valid @RequestBody RegenerateTokenDto tokenDto) {
        return this.userService.regenerateAccessToken(tokenDto).map(ResponseEntity::ok);
    }
}
