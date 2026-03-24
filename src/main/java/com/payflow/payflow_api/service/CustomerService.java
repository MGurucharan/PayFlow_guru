package com.payflow.payflow_api.service;

import com.payflow.payflow_api.dto.CustomerDTO;
import com.payflow.payflow_api.entity.Customer;
import com.payflow.payflow_api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository)
    {
        this.customerRepository=customerRepository;
    }

    public CustomerDTO createCustomer(CustomerDTO dto)
    {
        Customer customer=new Customer();
        customer.setName(dto.name());
        customer.setEmail(dto.email());

        Customer savedCustomer=customerRepository.save(customer);

        return convertToDTO(savedCustomer);
    }

    public List<CustomerDTO> getCustomers()
    {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long id)
    {
        Customer customer = customerRepository.findById(id).orElseThrow(()->new RuntimeException("Customer not found"));
        return convertToDTO(customer);
    }

    public CustomerDTO updateCustomer(Long id,CustomerDTO dto)
    {
        Customer customer=customerRepository.findById(id).orElseThrow(()->new RuntimeException("Customer not found !"));

        customer.setName(dto.name());
        customer.setEmail(dto.email());

        Customer updatedCustomer=customerRepository.save(customer);

        return convertToDTO(updatedCustomer);
    }

    public void deleteCustomer(Long id)
    {
        if(!customerRepository.existsById(id))
        {
            throw new RuntimeException("Customer not found");
        }

        customerRepository.deleteById(id);
    }

    private CustomerDTO convertToDTO(Customer customer)
    {
        return new CustomerDTO(customer.getId(),customer.getName(),customer.getEmail());
    }
}
