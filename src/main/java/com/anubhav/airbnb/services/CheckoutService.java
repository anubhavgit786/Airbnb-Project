package com.anubhav.airbnb.services;

import com.anubhav.airbnb.models.Booking;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

public interface CheckoutService
{
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
    String extractSessionId(Event event);
    void cancelSession(Session session) throws StripeException;
}
