package com.payflow.payflow_api.controller;

import com.payflow.payflow_api.dto.PaymentMethodDTO;
import com.payflow.payflow_api.dto.SetupIntentDTO;
import com.payflow.payflow_api.service.StripeCustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    private final StripeCustomerService stripeCustomerService;

    @Autowired
    public StripeController(StripeCustomerService stripeCustomerService) {
        this.stripeCustomerService = stripeCustomerService;
    }

    @PostMapping("/setup-intent/{customerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SetupIntentDTO getClientSecret(@Valid @PathVariable Long customerId)
    {
        return stripeCustomerService.createSetupIntent(customerId); // returns the DTO that contains the ClientSecret Key
    }

    @PostMapping("/payment-method")
    @ResponseStatus(HttpStatus.OK)
    public void postPaymentMethod( @RequestBody PaymentMethodDTO paymentMethodDTO)
    {
        stripeCustomerService.savePaymentMethod(paymentMethodDTO);
    }
}
