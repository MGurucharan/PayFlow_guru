package com.payflow.payflow_api.service;
import java.util.Random;

import com.payflow.payflow_api.dto.CreateInvoiceDTO;
import com.payflow.payflow_api.dto.InvoiceDTO;
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


    public BillingService(PlanRepository planRepository, InvoiceService invoiceService, SubscriptionRepository subscriptionRepository) {
        this.planRepository = planRepository;
        this.invoiceService=invoiceService;
        this.subscriptionRepository = subscriptionRepository;
    }


    // New Subscriptions/ Failed Subscriptions arrive :
    // Billing Service performs the billing at the time of payment
    public InvoiceDTO processSubscription(Subscription subscription,boolean isRetry)
    {

        Plan plan=planRepository.findById(subscription.getPlanId()).orElseThrow(()->new RuntimeException("Plan not found"));

        InvoiceStatus status;
        Random random = new Random();
        if(subscription.getBillingMode()== BillingMode.AUTO)
        {
            //Simulating the PAYMENT
            double perc=random.nextDouble(); // 0.54 , 0.23434 // RAZORPAY
            subscription.setPerc(perc);
            if((perc)>=0.70) // Successs !!!
            {
                if(isRetry)
                {
                    Integer currentRetryCount=subscription.getRetryCount();
                    // Performing the Retry so increment the retryCount by 1
                    subscription.setRetryCount(currentRetryCount+1);
                }

                status=InvoiceStatus.PAID;
                subscription.setStatus(SubscriptionStatus.ACTIVE); // Subscription still active !!!
                subscription.setRetryCount(0); // previous retryCount is reset to 0 for next billing cycle
            }
            else // Failed !!! can retry
            {
                status=InvoiceStatus.FAILED;

                // Retry Logic over here ?
                Integer currentRetryCount=subscription.getRetryCount();

                // Performing the Retry so increment the retryCount by 1
                if(isRetry)
                {
                    subscription.setRetryCount(currentRetryCount+1);
                }

                // Check if it has exceeded the maxRetryCount
                if(subscription.getRetryCount()>=subscription.getMaxRetryCount())
                {
                    subscription.setStatus(SubscriptionStatus.PAST_DUE); // Maybe Change it to CANCELLED when RetryCount exceeded ?
                }
                else
                {
                    subscription.setStatus(SubscriptionStatus.ACTIVE);
                }
            }
            subscriptionRepository.save(subscription);
        }
        else
        {
            status=InvoiceStatus.PENDING;
        }

        if(!isRetry)
        {
            CreateInvoiceDTO cinvoiceDTO=new CreateInvoiceDTO(subscription.getId(),plan.getPrice());

            return invoiceService.createInvoice(cinvoiceDTO,status);
        }
        else
        {
            // GET THE OLD INVOICE AND UPDATE THE STATUS TO PAID
            return invoiceService.updateInvoiceStatus(subscription.getId(),status);
        }

    }


}
