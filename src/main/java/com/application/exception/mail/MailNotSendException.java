package com.application.exception.mail;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MailNotSendException extends ResponseStatusException {
    public MailNotSendException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email!");
    }
}
