package com.api.erp.v1.main.features.endereco.domain.controller;

import com.api.erp.v1.main.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.main.features.endereco.application.dto.response.EnderecoResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IEnderecoController {

    public ResponseEntity<EnderecoResponse> criar(CreateEnderecoRequest request);

    public ResponseEntity<EnderecoResponse> buscar(Long id);

    public ResponseEntity<List<EnderecoResponse>> listar();

    public ResponseEntity<EnderecoResponse> atualizar(Long id, CreateEnderecoRequest request);

    public ResponseEntity<Void> deletar(Long id);
}
