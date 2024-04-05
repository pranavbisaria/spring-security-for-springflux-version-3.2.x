package com.application.repository;

import com.application.entity.OTP;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPRepository extends ReactiveMongoRepository<OTP, String> {
}
