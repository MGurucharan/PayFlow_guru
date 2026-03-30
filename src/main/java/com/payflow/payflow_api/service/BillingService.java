package com.payflow.payflow_api.service;
import java.util.Random;

import com.payflow.payflow_api.dto.CreateInvoiceDTO;
import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.enums.BillingMode;
import com.payflow.payflow_api.enums.InvoiceStatus;
import com.payflow.payflow_api.enums.SubscriptionStatus;
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

    public InvoiceDTO processSubscription(Subscription subscription)
    {

        Plan plan=planRepository.findById(subscription.getPlanId()).orElseThrow(()->new RuntimeException("Plan not found"));

        InvoiceStatus status;
        Random random = new Random();
        if(subscription.getBillingMode()== BillingMode.AUTO)
        {
            //Simulating the PAYMENT
            double perc=random.nextDouble(); // 0.54 , 0.23434
            if((perc)>=0.70)
            {
                status=InvoiceStatus.PAID;
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setRetryCount(0);
            }
            else
            {
                status=InvoiceStatus.FAILED;

                // Retry Logic over here ?
                Integer currentRetryCount=subscription.getRetryCount();
                subscription.setRetryCount(currentRetryCount+1);

                if(subscription.getRetryCount()>=subscription.getMaxRetryCount())
                {
                    subscription.setStatus(SubscriptionStatus.PAST_DUE);
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

        CreateInvoiceDTO cinvoiceDTO=new CreateInvoiceDTO(subscription.getId(),plan.getPrice());

         return invoiceService.createInvoice(cinvoiceDTO,status);

    }


}
