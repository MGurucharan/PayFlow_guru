package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.CreateInvoiceDTO;
import com.payflow.payflow_api.dto.CustomerDTO;
import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.entity.Invoice;
import com.payflow.payflow_api.entity.Subscription;
import com.payflow.payflow_api.enums.InvoiceStatus;
import com.payflow.payflow_api.repository.InvoiceRepository;
import com.payflow.payflow_api.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, SubscriptionRepository subscriptionRepository)
    {
        this.invoiceRepository=invoiceRepository;
        this.subscriptionRepository=subscriptionRepository;
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

        Invoice sinvoice=invoiceRepository.save(invoice);
        return convertToDTO(sinvoice);
    }

    public List<InvoiceDTO> getInvoices()
    {
        return invoiceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
