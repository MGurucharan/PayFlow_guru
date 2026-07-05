package com.payflow.payflow_api.dto;

public record PaymentResultDTO(        boolean success,
                                       String transactionId,
                                       String failureReason) {
}
