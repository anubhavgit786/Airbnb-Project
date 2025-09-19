package com.anubhav.airbnb.services;

import com.anubhav.airbnb.models.Booking;
import com.anubhav.airbnb.models.User;
import com.anubhav.airbnb.repositories.BookingRepository;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.exception.StripeException;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService
{
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl)
    {
        User user = userService.getCurrentUser();

        try
        {

            Customer customer = createCustomer(user);
            Session session = createSession(customer, booking, successUrl, failureUrl);

            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);
            return session.getUrl();

        }
        catch (StripeException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Customer createCustomer(User user) throws StripeException
    {
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();
        return Customer.create(customerParams);
    }

    private ProductData createProductData(Booking booking) throws StripeException
    {
        return ProductData.builder()
                .setName(booking.getHotel().getName() +" : "+ booking.getRoom().getType())
                .setDescription("Booking ID: "+booking.getId())
                .build();
    }

    private PriceData createPriceData(Booking booking) throws StripeException
    {
        return PriceData.builder()
                .setCurrency("inr")
                .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                .setProductData(createProductData(booking))
                .build();
    }

    private LineItem createLineItem(Booking booking) throws StripeException
    {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(createPriceData(booking))
                .build();
    }

    private Session createSession(Customer customer, Booking booking, String successUrl, String failureUrl) throws StripeException
    {
        SessionCreateParams sessionParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                .setCustomer(customer.getId())
                .setSuccessUrl(successUrl)
                .setCancelUrl(failureUrl)
                .addLineItem(createLineItem(booking))
                .build();

        return Session.create(sessionParams);
    }


    public String extractSessionId(Event event)
    {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        return (session != null) ? session.getId() : null;
    }

    @Override
    public void cancelSession(Session session) throws StripeException
    {
        RefundCreateParams refundParams = RefundCreateParams
                .builder()
                .setPaymentIntent(session.getPaymentIntent())
                .build();

        Refund.create(refundParams);
    }




}
