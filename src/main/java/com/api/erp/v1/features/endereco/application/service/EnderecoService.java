package com.api.erp.v1.features.endereco.application.service;

import com.api.erp.v1.features.endereco.application.dto.CreateEnderecoRequest;
import com.api.erp.v1.features.endereco.application.dto.EnderecoResponse;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.v1.features.endereco.domain.validator.EnderecoValidator;
import com.api.erp.v1.shared.domain.exception.NotFoundException;
import com.api.erp.v1.shared.domain.valueobject.CEP;
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

    public EnderecoResponse criar(CreateEnderecoRequest request) {
        EnderecoValidator.validarEndereco(request.rua(), request.numero(), request.bairro(), request.cidade(), request.estado(), request.cep());

        Endereco endereco = new Endereco(request.rua(), request.numero(), request.bairro(), request.cidade(), request.estado(), request.cep());

        if (request.complemento() != null && !request.complemento().isBlank()) {
            endereco.setComplemento(request.complemento());
        }

        Endereco enderecoCriado = enderecoRepository.save(endereco);
        return converterParaResponse(enderecoCriado);
    }

    public EnderecoResponse buscarPorId(Long id) {
        Endereco endereco = enderecoRepository.findById(id).orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
        return converterParaResponse(endereco);
    }

    public List<EnderecoResponse> buscarTodos() {
        return enderecoRepository.findAll().stream().map(this::converterParaResponse).collect(Collectors.toList());
    }

    public EnderecoResponse atualizar(Long id, CreateEnderecoRequest request) {
        Endereco endereco = enderecoRepository.findById(id).orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));

        EnderecoValidator.validarEndereco(request.rua(), request.numero(), request.bairro(), request.cidade(), request.estado(), request.cep());

        endereco.setRua(request.rua());
        endereco.setNumero(request.numero());
        endereco.setComplemento(request.complemento());
        endereco.setBairro(request.bairro());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());
        endereco.setCep(new CEP(request.cep()));
        endereco.setDataAtualizacao(LocalDateTime.now());

        enderecoRepository.save(endereco);
        return converterParaResponse(endereco);
    }

    public void deletar(Long id) {
        enderecoRepository.findById(id).orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
        enderecoRepository.deleteById(id);
    }

    private EnderecoResponse converterParaResponse(Endereco endereco) {
        EnderecoResponse response = new EnderecoResponse(endereco.getId(), endereco.getRua(), endereco.getNumero(), endereco.getComplemento(), endereco.getBairro(),
                endereco.getCidade(), endereco.getEstado(), endereco.getCep(), endereco.getDataCriacao(), endereco.getDataAtualizacao());

        return response;
    }
}
