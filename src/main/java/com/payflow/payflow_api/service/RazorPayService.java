package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.PaymentResultDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;

public class RazorPayService {
    private final CustomerRepository customerRepository;

    @Value("${razorpay.api.secret}")
    private String razorpayApiSecret;

    @Value("${razorpay.api.key}")
    private String razorpayApiKey;

    public RazorPayService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public PaymentResultDTO createOrder(Subscription subscription, Double price) {
        Customer customer = customerRepository.findById(subscription.getCustomerId()).orElseThrow(() -> new RuntimeException("Customer not found"));




    }


}
