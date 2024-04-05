package com.application.repository;

import com.application.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmailAndIsActive(String email, Boolean isActive);

    Mono<Boolean> existsByUsername(String username);
}
