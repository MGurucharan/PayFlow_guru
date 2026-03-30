package com.payflow.payflow_api.entity;

import com.payflow.payflow_api.enums.BillingMode;
import com.payflow.payflow_api.enums.SubscriptionStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private Integer retryCount;

    private int maxRetryCount;

    private Long planId;

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = 0;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount() {
        this.maxRetryCount = 3;
    }

    @Enumerated (EnumType.STRING)
    private BillingMode billingMode;// Auto //Invoice

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private Double perc;

    public Double getPerc() {
        return perc;
    }

    public void setPerc(Double perc) {
        this.perc = perc;
    }

    private LocalDate startDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public BillingMode getBillingMode() {
        return billingMode;
    }

    public void setBillingMode(BillingMode billingMode) {
        this.billingMode = billingMode;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(LocalDate nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    private LocalDate nextBillingDate;
}


