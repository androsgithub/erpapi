package com.api.erp.features.endereco.infrastructure.persistence;

import com.api.erp.features.endereco.domain.entity.Endereco;
import com.api.erp.features.endereco.domain.repository.EnderecoRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class EnderecoRepositoryImpl implements EnderecoRepository {
    
    private final Map<String, Endereco> enderecos = new HashMap<>();
    
    @Override
    public Endereco salvar(Endereco endereco) {
        enderecos.put(endereco.getId(), endereco);
        return endereco;
    }
    
    @Override
    public Optional<Endereco> buscarPorId(String id) {
        return Optional.ofNullable(enderecos.get(id));
    }
    
    @Override
    public List<Endereco> buscarTodos() {
        return new ArrayList<>(enderecos.values());
    }
    
    @Override
    public void atualizar(Endereco endereco) {
        enderecos.put(endereco.getId(), endereco);
    }
    
    @Override
    public void deletar(String id) {
        enderecos.remove(id);
    }
}
