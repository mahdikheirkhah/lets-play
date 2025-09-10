package com.gritlab.lets_play.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // This annotation is a good practice
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}