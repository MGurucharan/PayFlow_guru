package com.payflow.payflow_api.controller;

import com.payflow.payflow_api.dto.PlanDTO;
import com.payflow.payflow_api.dto.SubscriptionDTO;
import com.payflow.payflow_api.dto.SubscriptionResponseDTO;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService)
    {
        this.subscriptionService=subscriptionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionResponseDTO createSubscription(@Valid @RequestBody SubscriptionDTO dto)
    {
        return subscriptionService.createSubscription(dto);
    }

    @GetMapping
    public List<SubscriptionDTO> getSubscriptions()
    {
        return subscriptionService.getSubscriptions();
    }

    @PutMapping("/{id}")
    public SubscriptionDTO updateSubscription(@Valid @PathVariable Long id,@RequestBody SubscriptionDTO dto)
    {
        return subscriptionService.updateSubscription(id,dto);
    }

    @GetMapping("/{id}")
    public SubscriptionDTO getInvoiceById(@Valid @PathVariable Long id)
    {
        return subscriptionService.getSubscriptionById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@Valid @PathVariable Long id)
    {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
