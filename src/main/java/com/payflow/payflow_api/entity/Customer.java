package com.payflow.payflow_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Customer{
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    private String stripeCustomerId;

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public String getStripePaymentMethodId() {
        return stripePaymentMethodId;
    }

    public void setStripePaymentMethodId(String stripePaymentMethodId) {
        this.stripePaymentMethodId = stripePaymentMethodId;
    }

    private String stripePaymentMethodId;

    public void setEmail(String email) {
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // OverPayment Features

    private Double creditBalance;

    public Double getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Double creditBalance) {
        this.creditBalance = creditBalance;
    }


    private String name;

    private String email;
}
