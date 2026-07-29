package com.payflow.payflow_api.dto;

public record RazorPayCallbackDTO(      String razorpayOrderId,
                                        String razorpayPaymentId,
                                        String razorpaySignature ) {
}
