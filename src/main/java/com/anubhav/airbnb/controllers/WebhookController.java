package com.anubhav.airbnb.controllers;


import com.anubhav.airbnb.services.WebhookService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController
{
    private final WebhookService webhookService;

    @PostMapping("/payment")
    //@Operation(summary = "Capture the payments", tags = {"Webhook"})
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader)
    {
        webhookService.capturePayment(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

}
