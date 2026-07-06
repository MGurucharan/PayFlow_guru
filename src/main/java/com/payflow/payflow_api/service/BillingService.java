package com.payflow.payflow_api.service;
import java.time.LocalDate;
import java.util.Random;

import com.payflow.payflow_api.dto.CreateInvoiceDTO;
import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.dto.PaymentResultDTO;
import com.payflow.payflow_api.entity.Invoice;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.enums.BillingMode;
import com.payflow.payflow_api.enums.InvoiceStatus;
import com.payflow.payflow_api.enums.SubscriptionStatus;
import com.payflow.payflow_api.repository.InvoiceRepository;
import com.payflow.payflow_api.repository.PlanRepository;
import com.payflow.payflow_api.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    private final PlanRepository planRepository;
    private final InvoiceService invoiceService;
    private final SubscriptionRepository subscriptionRepository;
    private final StripePaymentService stripePaymentService;


    public BillingService(PlanRepository planRepository, InvoiceService invoiceService, SubscriptionRepository subscriptionRepository, StripePaymentService stripePaymentService) {
        this.planRepository = planRepository;
        this.invoiceService=invoiceService;
        this.subscriptionRepository = subscriptionRepository;
        this.stripePaymentService = stripePaymentService;
    }


    // New Subscriptions/ Failed Subscriptions arrive :
    // Billing Service performs the billing at the time of payment
    public InvoiceDTO processSubscription(Subscription subscription,boolean isRetry)
    {

        Plan plan=planRepository.findById(subscription.getPlanId()).orElseThrow(()->new RuntimeException("Plan not found"));

        InvoiceStatus status = InvoiceStatus.PENDING;

        /*
        Implement Razor Pay in BillingService itself !

        Flow

        Get the Subscription
        |
        Get the corresponding Plan
        |
        Initialize the Invoice status to PENDING ( depends on the RazorPay Success or Failure )
        |
        AUTO
        |
        Use StripePayment Auto recurring services
        |
        Retrieve Stored Payment Method
        |
        Create PaymentIntent
        |
        Charge the customer Automatically
        |
        Create/Update the invoice based on Success/Failed
        |
        MANUAL
        |
        Use RazorPay Payment Gateway
        |
        Create the order request
        |
        Return the order req to React
        |
        Customer sees a payment UI
        |
        Performs the payment by manually entering the payment details
        |
        Based on success/failure
        |
        Create the invoice based on Success/Failed

         */
        if(subscription.getBillingMode()== BillingMode.AUTO)
        {
            /*
                AUTO
                |
                Use StripePayment Auto recurring services
                |
                Retrieve Stored Payment Method
                |
                Create PaymentIntent
                |
                Charge the customer Automatically
                |
                Create/Update the invoice based on Success/Failed
                |
             */

            PaymentResultDTO paymentResultDTO=stripePaymentService.chargeCustomer(subscription,plan.getPrice());
            /*
            public record PaymentResultDTO(boolean success,
                                       String transactionId,
                                       String failureReason) {
            }

             */

            if(paymentResultDTO.success())
            {
                status=InvoiceStatus.PAID;
                subscription.setRetryCount(0);
                subscription.setStatus(SubscriptionStatus.ACTIVE);
            }
            else
            {
                status=InvoiceStatus.FAILED;
                if(isRetry)
                {
                    subscription.setRetryCount(
                            subscription.getRetryCount()+1);
                }
                if(subscription.getRetryCount() >=
                        subscription.getMaxRetryCount())
                {
                    subscription.setStatus(
                            SubscriptionStatus.PAST_DUE);
                }
            }
            return new InvoiceDTO(null, subscription.getId(), plan.getPrice(), LocalDate.now(),LocalDate.now(),status);
        }
        else
        {
            /*
            MANUAL
            |
            Use RazorPay Payment Gateway
            |
            Create the order request
            |
            Return the order req to React
            |
            Customer sees a payment UI
            |
            Performs the payment by manually entering the payment details
            |
            Based on success/failure
            |
            Create the invoice based on Success/Failed
             */

            status=InvoiceStatus.PENDING;
        }

//        if(!isRetry)
//        {
//            CreateInvoiceDTO cinvoiceDTO=new CreateInvoiceDTO(subscription.getId(),plan.getPrice());
//
//            return invoiceService.createInvoice(cinvoiceDTO,status);
//        }
//        else
//        {
//            // GET THE OLD INVOICE AND UPDATE THE STATUS TO PAID
//            return invoiceService.updateInvoiceStatus(subscription.getId(),status);
//        }

        return new InvoiceDTO(null,null,null,null,null,InvoiceStatus.PENDING);

    }


}
