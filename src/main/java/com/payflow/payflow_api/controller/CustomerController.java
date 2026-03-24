package com.payflow.payflow_api.controller;

import com.payflow.payflow_api.dto.CustomerDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService)
    {
        this.customerService=customerService;
    }

    // ONLY CREATION is there currently so :

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@Valid @RequestBody CustomerDTO dto)
    {
        return customerService.createCustomer(dto);
    }

    @GetMapping
    public List<CustomerDTO> getCustomers()
    {
        return customerService.getCustomers();
    }

    @PutMapping("/{id}")
    public CustomerDTO updateCustomer(@Valid @PathVariable Long id,@RequestBody CustomerDTO dto)
    {
        return customerService.updateCustomer(id,dto);
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomerById(@Valid @PathVariable Long id)
    {
        return customerService.getCustomerById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@Valid @PathVariable Long id)
    {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
