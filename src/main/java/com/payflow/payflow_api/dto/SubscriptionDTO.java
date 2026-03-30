package com.payflow.payflow_api.dto;

import com.payflow.payflow_api.enums.BillingMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubscriptionDTO(
        Long id,
        @NotNull
        Long customerId,

        @NotNull
        Long planId,

        @NotBlank
        BillingMode billingMode
)
{
}
