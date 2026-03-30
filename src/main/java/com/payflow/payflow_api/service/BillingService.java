package com.payflow.payflow_api.service;
import java.util.Random;

import com.payflow.payflow_api.dto.CreateInvoiceDTO;
import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.enums.InvoiceStatus;
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
        if(subscription.getBillingMode().equals("Auto"))
        {
            //Simulating the PAYMENT
            double perc=random.nextDouble(); // 0.54 , 0.23434
            if((perc*100)>=70)
            {
                status=InvoiceStatus.PAID;
            }
            else
            {
                status=InvoiceStatus.FAILED;
            }
        }
        else
        {
            status=InvoiceStatus.PENDING;
        }

        CreateInvoiceDTO cinvoiceDTO=new CreateInvoiceDTO(subscription.getId(),plan.getPrice());

         return invoiceService.createInvoice(cinvoiceDTO,status);

    }

}
