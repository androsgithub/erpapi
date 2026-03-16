package com.api.erp.v1.main.dynamic.features.address.domain.controller;

import com.api.erp.v1.main.dynamic.features.address.application.dto.request.CreateAddressRequest;
import com.api.erp.v1.main.dynamic.features.address.application.dto.response.AddressResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAddressController {

    public ResponseEntity<AddressResponse> criar(CreateAddressRequest request);

    public ResponseEntity<AddressResponse> buscar(Long id);

    public ResponseEntity<List<AddressResponse>> listar();

    public ResponseEntity<AddressResponse> atualizar(Long id, CreateAddressRequest request);

    public ResponseEntity<Void> deletar(Long id);
}
