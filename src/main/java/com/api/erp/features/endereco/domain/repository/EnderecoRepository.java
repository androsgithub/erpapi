package com.api.erp.features.endereco.domain.repository;

import com.api.erp.features.endereco.domain.entity.Endereco;
import java.util.List;
import java.util.Optional;

public interface EnderecoRepository {
    Endereco salvar(Endereco endereco);
    Optional<Endereco> buscarPorId(String id);
    List<Endereco> buscarTodos();
    void atualizar(Endereco endereco);
    void deletar(String id);
}
