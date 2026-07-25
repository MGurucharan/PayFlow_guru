package com.payflow.payflow_api.dto;

public record RazorPayOrderDTO(String orderId,Long amount,String currency,String key) {
}
