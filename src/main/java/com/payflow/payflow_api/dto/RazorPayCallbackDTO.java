package com.payflow.payflow_api.dto;

public record RazorPayCallbackDTO(
        String orderCreationId,
        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature ) {
}
