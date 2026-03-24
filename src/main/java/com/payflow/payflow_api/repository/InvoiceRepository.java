package com.payflow.payflow_api.repository;

import com.payflow.payflow_api.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
}
