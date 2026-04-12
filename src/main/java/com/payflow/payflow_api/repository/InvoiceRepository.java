package com.payflow.payflow_api.repository;

import com.payflow.payflow_api.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    Optional<Invoice> findTopBySubscriptionIdOrderByIdDesc(Long subscriptionId);
}
