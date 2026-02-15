package com.api.erp.v1.main.features.endereco.infrastructure.service;

import com.api.erp.v1.main.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.main.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.main.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.v1.main.features.endereco.domain.service.IEnderecoService;
import com.api.erp.v1.main.features.endereco.domain.validator.IEnderecoValidator;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.shared.domain.valueobject.CEP;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnderecoService implements IEnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final IEnderecoValidator validator;

    public Endereco criar(CreateEnderecoRequest request) {
        validator.validarCriacao(request);

        Endereco endereco = Endereco.builder()
                .rua(request.rua())
                .numero(request.numero())
                .complemento(request.complemento())
                .bairro(request.bairro())
                .cidade(request.cidade())
                .estado(request.estado())
                .cep(new CEP(request.cep()))
                .tipo(request.tipo())
                .principal(request.principal())
                .build();

        return enderecoRepository.save(endereco);
    }

    public Endereco buscarPorId(Long id) {
        validator.validarId(id);
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
    }

    public List<Endereco> buscarTodos() {
        return enderecoRepository.findAll();
    }

    public Endereco atualizar(Long id, CreateEnderecoRequest request) {
        validator.validarAtualizacao(id, request);

        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));

        endereco.setRua(request.rua());
        endereco.setNumero(request.numero());
        endereco.setComplemento(request.complemento());
        endereco.setBairro(request.bairro());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());
        endereco.setCep(new CEP(request.cep()));
        endereco.setTipo(request.tipo());
        endereco.setPrincipal(request.principal());

        return enderecoRepository.save(endereco);
    }

    public void deletar(Long id) {
        validator.validarId(id);
        enderecoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + id));
        enderecoRepository.deleteById(id);
    }
}
