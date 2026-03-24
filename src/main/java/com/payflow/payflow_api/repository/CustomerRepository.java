package com.payflow.payflow_api.repository;

import com.payflow.payflow_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

}
