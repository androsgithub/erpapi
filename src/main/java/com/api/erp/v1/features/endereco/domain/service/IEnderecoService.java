package com.api.erp.v1.features.endereco.domain.service;

import com.api.erp.v1.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;

import java.util.List;

public interface IEnderecoService {

    public Endereco criar(CreateEnderecoRequest request);

    public Endereco buscarPorId(Long id);

    public List<Endereco> buscarTodos();

    public Endereco atualizar(Long id, CreateEnderecoRequest request);

    public void deletar(Long id);
}
