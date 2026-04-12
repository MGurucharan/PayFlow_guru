package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.CreateInvoiceDTO;
import com.payflow.payflow_api.dto.CustomerDTO;
import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.dto.InvoicePayableDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.entity.Invoice;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.enums.InvoiceStatus;
import com.payflow.payflow_api.repository.CustomerRepository;
import com.payflow.payflow_api.repository.InvoiceRepository;
import com.payflow.payflow_api.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, SubscriptionRepository subscriptionRepository, CustomerRepository customerRepository)
    {
        this.invoiceRepository=invoiceRepository;
        this.subscriptionRepository=subscriptionRepository;
        this.customerRepository = customerRepository;
    }

    // Create an Invoice

    public InvoiceDTO createInvoice(CreateInvoiceDTO dto,InvoiceStatus status)
    {
        Invoice invoice=new Invoice();
        invoice.setAmount(dto.amount());
        invoice.setSubscriptionId(dto.subscriptionId());
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(15));
        invoice.setStatus(status);

        Invoice saved_invoice=invoiceRepository.save(invoice);
        return convertToDTO(saved_invoice);
    }

    public List<InvoiceDTO> getInvoices()
    {
        return invoiceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InvoicePayableDTO getPayableInvoice(Long invoiceId)
    {
        // get the corresponding invoice amount
        // get the creditBalance of the customer using the invoiceId ( invoiceId -> Invoice -> subscriptionId -> subscription -> customerId -> custoemr -> CreditBalance )
        // calc the finalPayable = max(invoiceAmount - customer.creditBalance, 0)
        // create and return the InvoicePayableDTO

        // Get the corresponding INVOICE based payment mode invoice

        Invoice invoice=invoiceRepository.findById(invoiceId).orElseThrow(()->new RuntimeException("invoice not found"));


        Double invoice_amount = invoice.getAmount();

        Long subscription_id=invoice.getSubscriptionId();

        Subscription subscription =subscriptionRepository.findById(subscription_id).orElseThrow(()->new RuntimeException("Subscription not found !"));

        Long customer_id=subscription.getCustomerId();

        Customer customer=customerRepository.findById(customer_id).orElseThrow(()->new RuntimeException("Customer not found !"));

        Double creditBalance=customer.getCreditBalance()==null?0.0:customer.getCreditBalance();

        Double final_payable=Math.max(invoice_amount-creditBalance,0);

        return new InvoicePayableDTO(invoice_amount,creditBalance,final_payable);

    }

    public InvoiceDTO getInvoiceById(Long id)
    {
        Invoice invoice=invoiceRepository.findById(id).orElseThrow(()->new RuntimeException("Invoice not found !"));

        return convertToDTO(invoice);
    }

    public InvoiceDTO updateInvoice(Long id,InvoiceDTO dto)
    {
        Invoice invoice =invoiceRepository.findById(id).orElseThrow(()->new RuntimeException("Invoice not found !"));

        invoice.setSubscriptionId(dto.subscriptionId());
        invoice.setAmount(dto.amount());

        Invoice updatedInvoice=invoiceRepository.save(invoice);

        return convertToDTO(updatedInvoice);
    }

    public void deleteInvoice(Long id)
    {
        if(!invoiceRepository.existsById(id))
        {
            throw new RuntimeException("Customer not found");
        }
        invoiceRepository.deleteById(id);
    }

    public InvoiceDTO convertToDTO(Invoice invoice)
    {
        return new InvoiceDTO(invoice.getId(),invoice.getSubscriptionId(),invoice.getAmount(),invoice.getIssueDate(),invoice.getDueDate(),invoice.getStatus());
    }
}
