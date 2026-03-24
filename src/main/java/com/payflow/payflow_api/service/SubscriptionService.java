package com.payflow.payflow_api.service;


import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.dto.SubscriptionDTO;
import com.payflow.payflow_api.dto.SubscriptionResponseDTO;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.enums.BillingCycle;
import com.payflow.payflow_api.enums.SubscriptionStatus;
import com.payflow.payflow_api.repository.PlanRepository;
import com.payflow.payflow_api.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final BillingService billingservice;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, PlanRepository planRepository, BillingService billingservice)
    {
        this.subscriptionRepository=subscriptionRepository;
        this.billingservice=billingservice;
        this.planRepository=planRepository;
    }

    public SubscriptionResponseDTO createSubscription(SubscriptionDTO dto)
    {
        Subscription subscription=new Subscription();
        subscription.setBillingMode(dto.billingMode());
        subscription.setCustomerId(dto.customerId());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setPlanId(dto.planId());
        subscription.setStartDate(LocalDate.now());

        Plan plan = planRepository.findById(dto.planId()).orElseThrow(()->new RuntimeException("Plan Not Found"));

        if(plan.getBillingCycle()== BillingCycle.MONTHLY)
        {
            subscription.setNextBillingDate(LocalDate.now().plusMonths(1));
        }
        else
        {
            subscription.setNextBillingDate(LocalDate.now().plusYears(1));
        }

        Subscription saved =  subscriptionRepository.save(subscription);

        // Pass the saved subscription to BillingService

        InvoiceDTO invoiceDTO = billingservice.processSubscription(saved);

        return new SubscriptionResponseDTO(convertToDTO(saved),invoiceDTO);
    }

    public List<SubscriptionDTO> getSubscriptions()
    {
        return subscriptionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SubscriptionDTO getSubscriptionById(Long id)
    {
        Subscription subscription=subscriptionRepository.findById(id).orElseThrow(()->new RuntimeException("Subscription not found !"));
        return convertToDTO(subscription);
    }

    public SubscriptionDTO updateSubscription(Long id,SubscriptionDTO dto)
    {
        Subscription subscription =subscriptionRepository.findById(id).orElseThrow(()->new RuntimeException("Subscription not found !"));

        subscription.setCustomerId(dto.customerId());
        subscription.setPlanId(dto.planId());
        subscription.setBillingMode(dto.billingMode());

        Subscription updatedSubscription=subscriptionRepository.save(subscription);

        return convertToDTO(updatedSubscription);
    }

    public void deleteSubscription(Long id)
    {
        if(!subscriptionRepository.existsById(id))
        {
            throw new RuntimeException("Subscription not found !");
        }
        subscriptionRepository.deleteById(id);
    }

    public SubscriptionDTO convertToDTO(Subscription subscription)
    {
        return new SubscriptionDTO(subscription.getId(), subscription.getCustomerId(),subscription.getPlanId(),subscription.getBillingMode());
    }
}
