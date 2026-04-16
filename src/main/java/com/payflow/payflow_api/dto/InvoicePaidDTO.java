package com.payflow.payflow_api.dto;

import com.payflow.payflow_api.enums.InvoiceStatus;

public record InvoicePaidDTO(Double creditBalance, Double invoiceAmount,
                             InvoiceStatus invoiceStatus, Double amountPaid, Double dueAmount) {
}
