package com.application.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
@SecurityRequirement(name = "Bearer Token")
public class MainController {

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("Token is validated successfully!!");
    }

    @GetMapping("/user-details")
    public Mono<String> getUserDetails(@AuthenticationPrincipal Mono<UserDetails> userDetails) {
        return userDetails.map( p -> "Hello " + p.getUsername()).switchIfEmpty(Mono.just("Not working"));
    }
}