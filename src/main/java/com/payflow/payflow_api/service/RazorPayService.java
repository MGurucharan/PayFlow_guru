package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.PaymentResultDTO;
import com.payflow.payflow_api.dto.RazorPayOrderDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.repository.CustomerRepository;
import com.payflow.payflow_api.repository.PlanRepository;
import com.payflow.payflow_api.repository.SubscriptionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RazorPayService {
    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${razorpay.api.secret}")
    private String razorpayApiSecret;

    @Value("${razorpay.api.key}")
    private String razorpayApiKey;

    public RazorPayService(CustomerRepository customerRepository, PlanRepository planRepository, SubscriptionRepository subscriptionRepository) {
        this.customerRepository = customerRepository;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public RazorPayOrderDTO createOrder(Long subscId, Long price) {
        Subscription subscription = subscriptionRepository.findById(subscId).orElseThrow(()->new RuntimeException("Subscription not found"));
        Customer customer = customerRepository.findById(subscription.getCustomerId()).orElseThrow(() -> new RuntimeException("Customer not found"));
        Plan plan=planRepository.findById(subscription.getPlanId()).orElseThrow(() -> new RuntimeException("Plan not found"));
        try
        {
            RazorpayClient razorpay = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
            JSONObject request = new JSONObject();
            request.put("amount",plan.getPrice()*100);  // Razorpay expects amount in paise
            request.put("currency", "INR");

            Order order = razorpay.orders.create(request); // creation of the order object with the required parameters

            String orderId = order.get("id").toString();
            Long amount = ((Number) order.get("amount")).longValue();
            String currency = order.get("currency").toString();

            return new RazorPayOrderDTO(orderId,amount,currency,razorpayApiKey);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Razorpay API Error: " + e.getMessage());
        }

    }
}
