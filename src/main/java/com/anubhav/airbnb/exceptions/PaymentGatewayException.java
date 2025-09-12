package com.anubhav.airbnb.exceptions;

import lombok.Getter;

@Getter
public class PaymentGatewayException extends RuntimeException
{
    private String provider;

    public PaymentGatewayException(String message)
    {
        super(message);
    }

    public PaymentGatewayException(String provider, String message, Throwable cause)
    {
        super(message, cause);
        this.provider = provider;
    }
}
