package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.PaymentResultDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.repository.CustomerRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    private final CustomerRepository customerRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public StripePaymentService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public PaymentResultDTO chargeCustomer(Subscription subscription, Double price) // Creates the paymentIntent on the Saved Payment method and charges the customer
    {

        Customer customer = customerRepository.findById(subscription.getCustomerId()).orElseThrow(()->new RuntimeException("Customer not found"));

        String stripeCustomerId=customer.getStripeCustomerId();
        String stripePaymentMethodId=customer.getStripePaymentMethodId();


        Stripe.apiKey = stripeSecretKey;

        if (stripeCustomerId == null || stripePaymentMethodId == null) {
            return new PaymentResultDTO(
                    false,
                    null,
                    "Customer has no saved payment method"
            );
        }

        // setting all the parameters required for PaymentIntent
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()

                        .setAmount((long)(price*100))        // in paise/cents
                        .setCurrency("inr")

                        .setCustomer(stripeCustomerId)

                        .setPaymentMethod(stripePaymentMethodId)


                        .setOffSession(true)

                        .setConfirm(true)

                        .build();

        try
        {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            if ("succeeded".equals(paymentIntent.getStatus()))
            {
                return new PaymentResultDTO
                        (
                                true,
                                paymentIntent.getId(),
                                null
                        );
            }
            else
            {
                String reason = paymentIntent.getLastPaymentError() != null
                        ? paymentIntent.getLastPaymentError().getMessage()
                        : paymentIntent.getStatus();
                return new PaymentResultDTO(false,paymentIntent.getId(),reason);
            }
        }
        catch(StripeException e)
        {
            return new PaymentResultDTO(
                    false,
                    null,
                    e.getMessage()
            );
        }

    }
}
