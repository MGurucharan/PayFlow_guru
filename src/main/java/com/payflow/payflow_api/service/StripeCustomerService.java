package com.payflow.payflow_api.service;


import com.payflow.payflow_api.dto.PaymentMethodDTO;
import com.payflow.payflow_api.dto.SetupIntentDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.repository.CustomerRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.SetupIntent;
import com.stripe.param.SetupIntentCreateParams;
import com.stripe.param.CustomerCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeCustomerService {

    private final CustomerRepository customerRepository;

    public StripeCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void createStripeCustomer(Customer customer) {
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(customer.getName())
                        .setEmail(customer.getEmail())
                        .build();

        try
        {
            com.stripe.model.Customer stripeCustomer =
                    com.stripe.model.Customer.create(params);

            customer.setStripeCustomerId(stripeCustomer.getId());

            customerRepository.save(customer);

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    // returns client-secret to the react frontend
    public SetupIntentDTO createSetupIntent(Long customerId)
    {
        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new RuntimeException("Customer not found"));
        if(customer.getStripeCustomerId()==null)
        {
            throw new RuntimeException(
                    "Stripe customer has not been created");
        }
            try
            {
                SetupIntentCreateParams params=SetupIntentCreateParams.builder()
                        .setCustomer(customer.getStripeCustomerId())
                        .setAutomaticPaymentMethods(SetupIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                        )
                        .build();

                SetupIntent setupIntent=SetupIntent.create(params);

                return new SetupIntentDTO(setupIntent.getClientSecret()) ;
            }
            catch (StripeException e)
            {
                throw new RuntimeException("Unable to create SetupIntent", e);
            }
    }

    public void savePaymentMethod(PaymentMethodDTO paymentMethodDTO)
    {
        Customer customer =
                customerRepository.findById(paymentMethodDTO.customerId())
                        .orElseThrow(() ->
                                new RuntimeException("Customer not found"));

        customer.setStripePaymentMethodId(
                paymentMethodDTO.paymentMethodId()
        );

        customerRepository.save(customer);
    }
}
