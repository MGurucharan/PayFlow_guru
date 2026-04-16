package com.payflow.payflow_api.dto;

import com.payflow.payflow_api.enums.InvoiceStatus;

public record InvoicePaidResponseDTO(Double creditBalance, Double invoiceAmount,
                                     InvoiceStatus invoiceStatus, Double amountPaid, Double dueAmount) {
}
