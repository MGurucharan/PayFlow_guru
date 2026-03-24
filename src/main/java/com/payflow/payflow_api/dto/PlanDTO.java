package com.payflow.payflow_api.dto;

import com.payflow.payflow_api.enums.BillingCycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlanDTO(
        Long id,
        @NotBlank(message = "Plan name required")
        String planName,

        @NotNull
        @Positive(message = "Price must be positive")
        Double price,

        @NotNull
        BillingCycle billingCycle
)
{
}
