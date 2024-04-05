package com.application.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "otp")
public class OTP {
    private static final long TTL_MINUTES = 10;

    @Id
    private String email;
    private String code;
    private LocalDateTime expiresOn;
    private Boolean verified;

    @Transient
    private final Random rand = new Random();

    public OTP(String email) {
        this.email = email;
        this.expiresOn = LocalDateTime.now().plusMinutes(TTL_MINUTES);
        this.code = generateCode();
        this.verified = false;
    }

    public void update() {
        this.code = generateCode();
        this.verified = false;
        this.expiresOn = LocalDateTime.now().plusMinutes(TTL_MINUTES);
    }

    private String generateCode() {
        return String.valueOf(rand.nextInt(900000) + 100000);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OTP)) return false;
        OTP otp = (OTP) o;
        return Objects.equals(code, otp.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
