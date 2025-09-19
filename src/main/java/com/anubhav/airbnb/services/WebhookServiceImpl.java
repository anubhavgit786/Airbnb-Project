package com.anubhav.airbnb.services;

import com.anubhav.airbnb.exceptions.BadRequestException;
import com.stripe.exception.SignatureVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.net.Webhook;
import com.stripe.model.Event;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService
{
    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Override
    public void capturePayment(String payload, String sigHeader)
    {
        try
        {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            bookingService.capturePayment(event);
        }
        catch (SignatureVerificationException e)
        {
            throw new BadRequestException(e.getMessage());
        }
    }
}
