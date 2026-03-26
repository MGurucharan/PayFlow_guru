package com.payflow.payflow_api.service;

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
        if(subscription.getBillingMode().equals("Auto"))
        {
            // Perform the Payment
            // If payment successful then mark the status as PAID
            // Else PENDING
            status=InvoiceStatus.PAID;
        }
        else
        {
            status=InvoiceStatus.PENDING;
        }

        CreateInvoiceDTO cinvoiceDTO=new CreateInvoiceDTO(subscription.getId(),plan.getPrice());

         return invoiceService.createInvoice(cinvoiceDTO,status);

    }

}
