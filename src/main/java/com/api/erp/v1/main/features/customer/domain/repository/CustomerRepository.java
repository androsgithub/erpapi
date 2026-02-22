package com.api.erp.v1.main.features.customer.domain.repository;

import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
