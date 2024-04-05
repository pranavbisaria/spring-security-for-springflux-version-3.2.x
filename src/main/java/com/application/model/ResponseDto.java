package com.application.model;

import org.springframework.http.HttpStatus;

public record ResponseDto (
    String message,
    HttpStatus status
){}
