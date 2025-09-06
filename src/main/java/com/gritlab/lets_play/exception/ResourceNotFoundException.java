package com.gritlab.lets_play.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception that is thrown when a requested resource (like a User or Product)
 * cannot be found in the database.
 * The @ResponseStatus annotation ensures that if this exception is unhandled,
 * it will automatically result in a 404 NOT_FOUND HTTP response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
