package com.api.erp.features.empresa.domain.repository;

import com.api.erp.features.empresa.domain.entity.Empresa;
import java.util.List;
import java.util.Optional;

public interface EmpresaRepository {
    Empresa salvar(Empresa empresa);
    Optional<Empresa> buscarPorId(String id);
    Optional<Empresa> buscarPorCnpj(String cnpj);
    Optional<Empresa> buscarPorEmail(String email);
    List<Empresa> buscarTodas();
    List<Empresa> buscarAativas();
    void atualizar(Empresa empresa);
    void deletar(String id);
}
