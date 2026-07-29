package com.payflow.payflow_api.dto;


import com.payflow.payflow_api.enums.InvoiceStatus;

public record BillingResultDTO(InvoiceDTO invoiceDTO, RazorPayOrderDTO razorPayOrderDTO) {
}
