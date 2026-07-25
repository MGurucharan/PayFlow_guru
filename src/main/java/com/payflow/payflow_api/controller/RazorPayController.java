package com.payflow.payflow_api.controller;

import com.payflow.payflow_api.dto.RazorPayOrderDTO;
import com.payflow.payflow_api.service.RazorPayService;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/razorpay")
public class RazorPayController {
    @Autowired
    private RazorPayService razorPayService;

    @GetMapping("/create/{id}/{amount}")
    public RazorPayOrderDTO getRazorPayService(@PathVariable Long id, @PathVariable Long amount)
    {
        return razorPayService.createOrder(id,amount);
    }
}
