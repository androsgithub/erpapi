package com.api.erp.features.empresa.infrastructure.persistence;

import com.api.erp.features.empresa.domain.entity.Empresa;
import com.api.erp.features.empresa.domain.repository.EmpresaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class EmpresaRepositoryImpl implements EmpresaRepository {
    
    private final Map<String, Empresa> empresas = new HashMap<>();
    
    @Override
    public Empresa salvar(Empresa empresa) {
        empresas.put(empresa.getId(), empresa);
        return empresa;
    }
    
    @Override
    public Optional<Empresa> buscarPorId(String id) {
        return Optional.ofNullable(empresas.get(id));
    }
    
    @Override
    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        return empresas.values().stream()
            .filter(e -> e.getCnpj().equals(cnpj))
            .findFirst();
    }
    
    @Override
    public Optional<Empresa> buscarPorEmail(String email) {
        return empresas.values().stream()
            .filter(e -> e.getEmail().equals(email))
            .findFirst();
    }
    
    @Override
    public List<Empresa> buscarTodas() {
        return new ArrayList<>(empresas.values());
    }
    
    @Override
    public List<Empresa> buscarAativas() {
        return empresas.values().stream()
            .filter(Empresa::isAtiva)
            .collect(Collectors.toList());
    }
    
    @Override
    public void atualizar(Empresa empresa) {
        empresas.put(empresa.getId(), empresa);
    }
    
    @Override
    public void deletar(String id) {
        empresas.remove(id);
    }
}
