package com.payflow.payflow_api.controller;

import com.payflow.payflow_api.dto.RazorPayOrderDTO;
import com.payflow.payflow_api.service.RazorPayService;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/razorpay")
public class RazorPayController {
    @Autowired
    private RazorPayService razorPayService;

    @GetMapping
    public RazorPayOrderDTO getRazorPayService(@RequestParam Long id,@RequestParam Long amount)
    {
        return razorPayService.createOrder(id,amount);
    }
}
