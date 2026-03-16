package com.api.erp.v1.main.dynamic.features.address.domain.service;

import com.api.erp.v1.main.dynamic.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.dynamic.features.address.domain.entity.Address;

import java.util.List;

public interface IAddressService {

    public Address criar(CreateAddressRequest request);

    public Address buscarPorId(Long id);

    public List<Address> buscarTodos();

    public Address atualizar(Long id, CreateAddressRequest request);

    public void deletar(Long id);
}
