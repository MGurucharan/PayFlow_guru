package com.payflow.payflow_api.dto;

public record PayInvoiceDTO(Long invoiceId,Double amountPaying,Boolean useWallet) {
}
