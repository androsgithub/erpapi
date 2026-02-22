package com.api.erp.v1.main.features.customer.domain.extension;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;

public interface ICustomerExtension {
    void antesDeCriar(Customer customer, CreateCustomerDto dto);

    void depoisDeCriar(Customer customer, CreateCustomerDto dto);

    void antesDeAtualizar(Customer customer, CreateCustomerDto dto);

    void depoisDeAtualizar(Customer customer, CreateCustomerDto dto);

    void antesDeDeletar(Customer customer);

    void depoisDeDeletar(Customer customer);

    String getTenantCode();

    boolean isEnabled();
}
