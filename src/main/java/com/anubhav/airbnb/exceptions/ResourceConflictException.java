package com.anubhav.airbnb.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceConflictException extends RuntimeException
{
    private final HttpStatus status;

    public ResourceConflictException(String message)
    {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }
}

