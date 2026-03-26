package com.payflow.payflow_api.controller;


import com.payflow.payflow_api.dto.CreateInvoiceDTO;
import com.payflow.payflow_api.dto.CustomerDTO;
import com.payflow.payflow_api.dto.InvoiceDTO;
import com.payflow.payflow_api.entity.Invoice;
import com.payflow.payflow_api.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService)
    {
        this.invoiceService=invoiceService;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@Valid @PathVariable Long id)
    {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }


}
