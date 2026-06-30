package com.payflow.payflow_api.dto;

import jakarta.validation.constraints.Positive;

public record InvoicePayableDTO(    @Positive Double amount,
                                    @Positive Double creditBalance,
                                    @Positive Double DueAmount,
                                    @Positive Double netPayable) {
}
