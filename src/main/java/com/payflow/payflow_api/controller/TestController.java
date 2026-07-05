package com.payflow.payflow_api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${stripe.secret.key}")
    private String key;

    @GetMapping("/test")
    public String test() {
        return key == null ? "NULL" : key.substring(0, 8);
    }
}