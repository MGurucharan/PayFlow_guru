package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.InvoicePaidResponseDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.entity.Invoice;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.enums.InvoiceStatus;
import com.payflow.payflow_api.repository.CustomerRepository;
import com.payflow.payflow_api.repository.InvoiceRepository;
import com.payflow.payflow_api.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentSettlementService {
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PaymentSettlementService(CustomerRepository customerRepository, InvoiceRepository invoiceRepository, SubscriptionRepository subscriptionRepository) {
        this.customerRepository = customerRepository;
        this.invoiceRepository=invoiceRepository;
        this.subscriptionRepository=subscriptionRepository;
    }

    public InvoicePaidResponseDTO payInvoice(Long invoiceId, Double amountPaying, Boolean useWallet)
    {
        Invoice invoice=invoiceRepository.findById(invoiceId).orElseThrow(()->new RuntimeException("Invoice not found"));

        Long subscriptionId=invoice.getSubscriptionId();

        Subscription subscription=subscriptionRepository.findById(subscriptionId).orElseThrow(()->new RuntimeException("Subscription not found"));

        Long customerId=subscription.getCustomerId();

        Customer customer=customerRepository.findById(customerId).orElseThrow(()->new RuntimeException("Customer not found"));

        Double creditBalance=customer.getCreditBalance(); // x

        Double invoiceAmount=invoice.getAmount(); // z


        // Perform the payment BASED ON useWallet or not

        Double extra=0.0;
        Double dueAmount=invoice.getDueAmount();

        InvoiceStatus invoiceStatus=invoice.getStatus();

        if(invoiceStatus.equals(InvoiceStatus.PAID))
        {
            throw new RuntimeException("Invoice already paid");
        }

        // USEWALLET = YES
        if(useWallet)
        {
            Double totalavailable = amountPaying+creditBalance; // y + x
            // Case - 1 ( Credit Balance handles everything ) :
            if(creditBalance>=invoiceAmount) // If he wants to use CreditBalance then check if CreditBalance can suffice everything
            {
                creditBalance=creditBalance-invoiceAmount;
                amountPaying=0.0;
                invoice.setStatus(InvoiceStatus.PAID);
                customer.setCreditBalance(creditBalance);
            }
            // Case - 2 ( Exact payment ) :
            // Start from here 17/4/2026

            else if(totalavailable.equals(invoiceAmount)) {
                creditBalance = 0.0; // remains as x only
                invoice.setStatus(InvoiceStatus.PAID);
                customer.setCreditBalance(creditBalance);
            }

            // Case - 2 ( Overpayment ) :
            else if ( totalavailable > invoiceAmount)
            {
                extra = totalavailable- invoiceAmount;
                creditBalance = extra; // remains as x only
                invoice.setStatus(InvoiceStatus.PAID);
                customer.setCreditBalance(creditBalance);
            }

            // Case - 3 ( Partial Payment ) :

            else if ( totalavailable < invoiceAmount)
            {
                dueAmount = invoiceAmount - (totalavailable);
                creditBalance = 0.0;
                invoice.setStatus(InvoiceStatus.PENDING);
                invoice.setDueAmount(dueAmount);
                customer.setCreditBalance(creditBalance);
            }
        }
        // USEWALLET = NO
        else
        {
            // CASE - 1 EXACT PAYMENT :
            if(amountPaying.equals(invoiceAmount))
            {
                invoice.setStatus(InvoiceStatus.PAID);
            }

            // CASE - 2 OVERPAYMENT :
            else if(amountPaying >  invoiceAmount)
            {
                extra=amountPaying-invoiceAmount;
                creditBalance=creditBalance+extra;
                invoice.setStatus(InvoiceStatus.PAID);
                customer.setCreditBalance(creditBalance);
            }

            else if(amountPaying <  invoiceAmount)
            {
                // Since he doesn't want to use his wallet so don't consider CreditBalance
                dueAmount = invoiceAmount-amountPaying;
                invoice.setDueAmount(dueAmount);
                invoice.setStatus(InvoiceStatus.PENDING);
            }
        }

        invoiceRepository.save(invoice);
        customerRepository.save(customer);


//        if(dueAmount<=0) // Handled by CreditBalance
//        {
//            creditBalance = -(dueAmount);
//            invoice.setStatus(InvoiceStatus.PAID);
//            customer.setCreditBalance(creditBalance);
//            invoiceRepository.save(invoice);
//            customerRepository.save(customer);
//            dueAmount=0.0;
//        }
//        else
//        {
//            creditBalance = 0.0; // CreditBalance exhausted COMPLETELY
//            invoice.setStatus(InvoiceStatus.PENDING);
//            customer.setCreditBalance(creditBalance);
//            invoiceRepository.save(invoice);
//            customerRepository.save(customer);
//        }

        return new InvoicePaidResponseDTO(creditBalance,invoiceAmount,invoice.getStatus(),amountPaying,dueAmount);
    }


}
