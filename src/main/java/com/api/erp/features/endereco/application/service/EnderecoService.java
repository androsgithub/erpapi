package com.api.erp.features.endereco.application.service;

import com.api.erp.features.endereco.application.dto.CriarEnderecoRequest;
import com.api.erp.features.endereco.application.dto.EnderecoResponse;
import com.api.erp.features.endereco.domain.entity.Endereco;
import com.api.erp.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.features.endereco.domain.validator.EnderecoValidator;
import com.api.erp.shared.domain.exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnderecoService {
    
    private final EnderecoRepository enderecoRepository;
    
    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }
    
    public EnderecoResponse criar(CriarEnderecoRequest request) {
        EnderecoValidator.validarEndereco(
            request.getRua(),
            request.getNumero(),
            request.getBairro(),
            request.getCidade(),
            request.getEstado(),
            request.getCep()
        );
        
        Endereco endereco = new Endereco(
            request.getRua(),
            request.getNumero(),
            request.getBairro(),
            request.getCidade(),
            request.getEstado(),
            request.getCep()
        );
        
        if (request.getComplemento() != null && !request.getComplemento().isBlank()) {
            endereco.setComplemento(request.getComplemento());
        }
        
        Endereco enderecoCriado = enderecoRepository.salvar(endereco);
        return converterParaResponse(enderecoCriado);
    }
    
    public EnderecoResponse buscarPorId(String id) {
        Endereco endereco = enderecoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
        return converterParaResponse(endereco);
    }
    
    public List<EnderecoResponse> buscarTodos() {
        return enderecoRepository.buscarTodos()
            .stream()
            .map(this::converterParaResponse)
            .collect(Collectors.toList());
    }
    
    public EnderecoResponse atualizar(String id, CriarEnderecoRequest request) {
        Endereco endereco = enderecoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
        
        EnderecoValidator.validarEndereco(
            request.getRua(),
            request.getNumero(),
            request.getBairro(),
            request.getCidade(),
            request.getEstado(),
            request.getCep()
        );
        
        endereco.setRua(request.getRua());
        endereco.setNumero(request.getNumero());
        endereco.setComplemento(request.getComplemento());
        endereco.setBairro(request.getBairro());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());
        endereco.setCep(new com.api.erp.shared.domain.valueobject.CEP(request.getCep()));
        endereco.setDataAtualizacao(LocalDateTime.now());
        
        enderecoRepository.atualizar(endereco);
        return converterParaResponse(endereco);
    }
    
    public void deletar(String id) {
        enderecoRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
        enderecoRepository.deletar(id);
    }
    
    private EnderecoResponse converterParaResponse(Endereco endereco) {
        EnderecoResponse response = new EnderecoResponse();
        response.setId(endereco.getId());
        response.setRua(endereco.getRua());
        response.setNumero(endereco.getNumero());
        response.setComplemento(endereco.getComplemento());
        response.setBairro(endereco.getBairro());
        response.setCidade(endereco.getCidade());
        response.setEstado(endereco.getEstado());
        response.setCep(endereco.getCep().getValor());
        response.setDataCriacao(endereco.getDataCriacao());
        response.setDataAtualizacao(endereco.getDataAtualizacao());
        return response;
    }
}
