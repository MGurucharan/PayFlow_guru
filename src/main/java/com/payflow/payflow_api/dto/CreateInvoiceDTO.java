package com.payflow.payflow_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateInvoiceDTO(
        @NotNull Long subscriptionId,
        @Positive Double amount
) {}
