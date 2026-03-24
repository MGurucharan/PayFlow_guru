package com.payflow.payflow_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubscriptionDTO(
        Long id,
        @NotNull
        Long customerId,

        @NotNull
        Long planId,

        @NotBlank
        String billingMode
)
{
}
