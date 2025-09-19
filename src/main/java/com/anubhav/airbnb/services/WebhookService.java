package com.anubhav.airbnb.services;


public interface WebhookService
{
    void capturePayment(String payload, String sigHeader);
}
