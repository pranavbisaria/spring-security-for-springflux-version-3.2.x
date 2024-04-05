package com.application.service;

import com.application.entity.OTP;
import com.application.exception.auth.InvalidOTPException;
import com.application.exception.auth.OTPExpiredException;
import com.application.exception.auth.UserAlreadyExistException;
import com.application.exception.user.UserNotFoundException;
import com.application.repository.OTPRepository;
import com.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPService {
    private final EmailService emailService;
    private final OTPRepository otpRepository;
    private final UserRepository userRepository;

    public Mono<Boolean> requestOTP(String email) {
        return this.otpRepository.save(new OTP(email))
                .flatMap(otp -> {
                    String subject = "OTP Verification";
                    String message = "Dear User," +
                            "\nThe One Time Password (OTP) to verify your Email Address is " + otp.getCode() +
                            "\nThe One Time Password is valid for the next 10 minutes." +
                            "\n(This is an auto generated email, so please do not reply back.)" +
                            "\nRegards," +
                            "\nTeam (SecurityApplicationDemo)";

                    return userRepository.existsByEmailAndIsActive(email, true).handle((res, sink) -> {
                                    if (Boolean.TRUE.equals(res)) {
                                        sink.error(new UserAlreadyExistException());
                                        return;
                                    }
                                    emailService.sendEmail(subject, message, email);
                                    sink.next(true);
                               });
                });
    }

    public Mono<Boolean> verifyOTP(String email, String code, boolean markedVerified) {
        return this.otpRepository.findById(email)
                .flatMap(otp -> {
                    if (otp.getCode().equals(code)) {
                        if (!Boolean.TRUE.equals(otp.getVerified()) && otp.getExpiresOn().isAfter(LocalDateTime.now())) {
                            otp.setVerified(markedVerified);
                            return (markedVerified) ? this.otpRepository.save(otp).then(Mono.just(true)) : Mono.just(true);
                        }
                        return Mono.error(new OTPExpiredException());
                    }
                    return Mono.error(new InvalidOTPException());
                }).switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    public void sendSuccessEmail (String email, String name) {
        String subject = "Successfully registered on (SecurityApplicationDemo)";
        String message = "Dear " + name + "," +
                "\nThank you for registering on (SecurityApplicationDemo)" +
                "\n(This is an auto generated email, so please do not reply back.)" +
                "\nRegards," +
                "\nTeam (SecurityApplicationDemo)";
        this.emailService.sendEmail(subject, message, email);
    }
}