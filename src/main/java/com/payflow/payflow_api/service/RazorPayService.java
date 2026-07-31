package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.dto.PaymentResultDTO;
import com.payflow.payflow_api.dto.RazorPayCallbackDTO;
import com.payflow.payflow_api.dto.RazorPayOrderDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.entity.Invoice;
import com.payflow.payflow_api.entity.Plan;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.enums.InvoiceStatus;
import com.payflow.payflow_api.repository.CustomerRepository;
import com.payflow.payflow_api.repository.InvoiceRepository;
import com.payflow.payflow_api.repository.PlanRepository;
import com.payflow.payflow_api.repository.SubscriptionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RazorPayService {
    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;

    @Value("${razorpay.api.secret}")
    private String razorpayApiSecret;

    @Value("${razorpay.api.key}")
    private String razorpayApiKey;

    public RazorPayService(CustomerRepository customerRepository, PlanRepository planRepository, SubscriptionRepository subscriptionRepository, InvoiceRepository invoiceRepository) {
        this.customerRepository = customerRepository;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public RazorPayOrderDTO createOrder(Long subscId) {
        Subscription subscription = subscriptionRepository.findById(subscId).orElseThrow(()->new RuntimeException("Subscription not found"));
        Customer customer = customerRepository.findById(subscription.getCustomerId()).orElseThrow(() -> new RuntimeException("Customer not found"));
        Plan plan=planRepository.findById(subscription.getPlanId()).orElseThrow(() -> new RuntimeException("Plan not found"));
        try
        {
            RazorpayClient razorpay = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
            JSONObject request = new JSONObject();
            long amountInPaise = Math.round(plan.getPrice() * 100);
            request.put("amount",amountInPaise);  // Razorpay expects amount in paise
            request.put("currency", "INR");

            Order order = razorpay.orders.create(request); // creation of the order object with the required parameters

            String orderId = order.get("id").toString();
            Long amount = ((Number) order.get("amount")).longValue();
            String currency = order.get("currency").toString();

            return new RazorPayOrderDTO(orderId,amount,currency,razorpayApiSecret);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Razorpay API Error: " + e.getMessage());
        }

    }

    public InvoiceDTO verifyPayment(Long invoiceId, RazorPayCallbackDTO razorPayCallbackDTO)
    {
        // Verification flow :

        // use the razorpayPaymentId and the orderCreationId to create HMAC hex digest  ( hash value ) and compare it with the
        // signature.

        JSONObject options=new JSONObject();
        options.put("razorpay_order_id", razorPayCallbackDTO.razorpayOrderId());
        options.put("razorpay_payment_id", razorPayCallbackDTO.razorpayPaymentId());
        options.put("razorpay_signature", razorPayCallbackDTO.razorpaySignature());

        System.out.println(razorPayCallbackDTO.razorpayOrderId());
        System.out.println(razorPayCallbackDTO.razorpayPaymentId());
        System.out.println(razorPayCallbackDTO.razorpaySignature());

        try {
           boolean isValid = Utils.verifyPaymentSignature(options,razorpayApiKey);

           if(isValid)
           {
               // update the invoice status to PAID
               Invoice invoice =invoiceRepository.findById(invoiceId).orElseThrow(()->new RuntimeException("Invoice not found"));
               invoice.setStatus(InvoiceStatus.PAID);
               invoiceRepository.save(invoice);

               return new InvoiceDTO(invoice.getId(),invoice.getSubscriptionId(),invoice.getAmount(),invoice.getIssueDate(),invoice.getDueDate(),invoice.getStatus());
           }
           else
           {
               throw new RuntimeException("Invalid signature because the signatures didnt match !");
           }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Razorpay API Error: " + e.getMessage());
        }
    }
}
