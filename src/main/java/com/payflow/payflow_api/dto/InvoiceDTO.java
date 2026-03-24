package com.payflow.payflow_api.dto;

import com.payflow.payflow_api.enums.InvoiceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public record InvoiceDTO(
        Long id,
        @NotNull Long subscriptionId,
        @Positive Double amount,
        LocalDate issueDate,
        LocalDate dueDate,
        InvoiceStatus invoiceStatus) {
}
