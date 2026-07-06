package com.payflow.payflow_api.service;


import com.payflow.payflow_api.dto.PaymentMethodDTO;
import com.payflow.payflow_api.dto.SetupIntentDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.repository.CustomerRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.SetupIntent;
import com.stripe.param.SetupIntentCreateParams;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeCustomerService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final CustomerRepository customerRepository;

    public StripeCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public void createStripeCustomer(Customer customer) {

        Stripe.apiKey = stripeSecretKey;
        // Listing out all the parameters required to create a Stripe Customer
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(customer.getName())
                        .setEmail(customer.getEmail())
                        .build();

        try
        {
            com.stripe.model.Customer stripeCustomer =
                    com.stripe.model.Customer.create(params); // Creating a Stripe Customer with params ( name , email )

            customer.setStripeCustomerId(stripeCustomer.getId()); // Once the Stripe customer is created , set the customer entity's stripeCustomerId with StripeCustomer's Id
            // Equivalent ~ Customer customer = client.v1().customers().create(params);

            customerRepository.save(customer); // saving the updated Customer entity in the DB

        } catch (StripeException e) { // Handle any exception
            throw new RuntimeException(e);
        }
    }

    // returns client-secret to the react frontend
    public SetupIntentDTO createSetupIntent(Long customerId)
    {
        Stripe.apiKey = stripeSecretKey;
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
                        .setCustomer(customer.getStripeCustomerId()) // ID of the Customer this SetupIntent belongs to, if one exists. and links the Payment method with the
                        // Customer having this stripeCustomerId.
                        .setAutomaticPaymentMethods
                          (
                                  SetupIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                          ) // Shows all the available payment methods to the customer for saving
                        .build();

                SetupIntent setupIntent=SetupIntent.create(params); //
                /*
                        DEMO response :

                     {
                      "id": "seti_1Mm8s8LkdIwHu7ix0OXBfTRG",
                      "object": "setup_intent",
                      "application": null,
                      "automatic_payment_methods": {
                        "enabled": true
                      },
                      "cancellation_reason": null,
                      "client_secret": "seti_1Mm8s8LkdIwHu7ix0OXBfTRG_secret_NXDICkPqPeiBTAFqWmkbff09lRmSVXe",
                      "created": 1678942624,
                      "customer": null,
                      "description": null,
                      "flow_directions": null,
                      "last_setup_error": null,
                      "latest_attempt": null,
                      "livemode": false,
                      "mandate": null,
                      "metadata": {},
                      "next_action": null,
                      "on_behalf_of": null,
                      "payment_method": null,
                      "payment_method_options": {
                        "card": {
                          "mandate_options": null,
                          "network": null,
                          "request_three_d_secure": "automatic"
                        }
                      },
                      "payment_method_types": [
                        "card"
                      ],
                      "single_use_mandate": null,
                      "status": "requires_payment_method",
                      "usage": "off_session"
                    }
                 */
                /*

                 */

                return new SetupIntentDTO(setupIntent.getClientSecret()) ;
            }
            catch (StripeException e)
            {
                throw new RuntimeException("Unable to create SetupIntent", e);
            }
    }

    public void savePaymentMethod(PaymentMethodDTO paymentMethodDTO)
    {
        Stripe.apiKey = stripeSecretKey;
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
