package com.payflow.payflow_api.controller;


import com.payflow.payflow_api.dto.*;
import com.payflow.payflow_api.service.InvoiceService;
import com.payflow.payflow_api.service.PaymentSettlementService;
import com.payflow.payflow_api.service.RazorPayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final PaymentSettlementService paymentSettlementService;
    private final RazorPayService razorPayService;

    public InvoiceController(InvoiceService invoiceService, PaymentSettlementService paymentSettlementService, RazorPayService razorPayService)
    {
        this.invoiceService=invoiceService;
        this.paymentSettlementService=paymentSettlementService;
        this.razorPayService = razorPayService;
    }


    @GetMapping
    public List<InvoiceDTO> getInvoices()
    {
        return invoiceService.getInvoices();
    }

    @PutMapping("/{id}")
    public InvoiceDTO updateInvoice(@Valid @PathVariable Long id,@RequestBody InvoiceDTO dto)
    {
        return invoiceService.updateInvoice(id,dto);
    }

    @GetMapping("/{id}")
    public InvoiceDTO getInvoiceById(@Valid @PathVariable Long id)
    {
        return invoiceService.getInvoiceById(id);
    }

    @GetMapping("/{invoiceId}/payable")
    public InvoicePayableDTO getPayableInvoice(@Valid @PathVariable Long invoiceId)
    {
        return invoiceService.getPayableInvoice(invoiceId);

        // Currently what shown :
        // Invoice amount
        // Credit Balance
        // netPayable ( Invoice - Credit Balance ) // Already you are including the CreditBalance
        // CHANGED to : Payable ( Invoice amount ONLY)

    }

    @PostMapping("/{invoiceId}/pay")
    public InvoicePaidResponseDTO payInvoice(@RequestBody PayInvoiceDTO payInvoiceDTO)
    {
        return paymentSettlementService.payInvoice(payInvoiceDTO.invoiceId(),payInvoiceDTO.amountPaying(),payInvoiceDTO.useWallet());
    }

    @PostMapping("/{invoiceId}/razorpay-callback")
    public InvoiceDTO VerifyPayment(@PathVariable Long invoiceId,@RequestBody RazorPayCallbackDTO razorPayCallbackDTO)
    {
        return razorPayService.verifyPayment(invoiceId,razorPayCallbackDTO);
    }


    // What all to show once a customer has paid ?
    // Credit Balance : x
    // Invoice amount : z
    // Invoice Status : PENDING/PAID
    // Amount Paid : y
    // Due Amount : w

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@Valid @PathVariable Long id)
    {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }


}
