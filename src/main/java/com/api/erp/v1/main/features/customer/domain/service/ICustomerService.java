package com.api.erp.v1.main.features.customer.domain.service;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICustomerService {
    public Page<Customer> pegarTodos(Pageable pageable);
    public Customer criar(CreateCustomerDto customerDto);
    public Customer atualizar(Long id, CreateCustomerDto customerDto);

    public Customer pegarPorId(Long id);

    public void deletar(Long id);
}
