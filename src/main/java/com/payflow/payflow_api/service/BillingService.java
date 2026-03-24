package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.CreateInvoiceDTO;
import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.repository.PlanRepository;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    private final PlanRepository planRepository;
    private final InvoiceService invoiceService;

    public BillingService(PlanRepository planRepository, InvoiceService invoiceService) {
        this.planRepository = planRepository;
        this.invoiceService=invoiceService;
    }

    public InvoiceDTO processSubscription(Subscription subscription)
    {

        Plan plan=planRepository.findById(subscription.getPlanId()).orElseThrow(()->new RuntimeException("Plan not found"));

        CreateInvoiceDTO invoiceDTO=new CreateInvoiceDTO(subscription.getId(),plan.getPrice());

         return invoiceService.createInvoice(invoiceDTO);

    }

}
