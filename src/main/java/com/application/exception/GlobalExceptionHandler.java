package com.application.exception;

import com.application.model.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ResponseDto>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto(ex.getMessage(), HttpStatus.NOT_FOUND)));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ResponseDto>> handleUserNotFoundException(ResponseStatusException ex) {
        return Mono.just(ResponseEntity.status(ex.getStatusCode())
                .body(new ResponseDto(ex.getMessage(), HttpStatus.resolve(ex.getStatusCode().value()))));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<ResponseDto>> handleBadCredentialException(BadCredentialsException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseDto(ex.getMessage(), HttpStatus.UNAUTHORIZED)));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleMethodArgumentNotValidException(WebExchangeBindException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return Mono.just(ResponseEntity.badRequest().body(errors));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ResponseDto>> handleException(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
    }
}