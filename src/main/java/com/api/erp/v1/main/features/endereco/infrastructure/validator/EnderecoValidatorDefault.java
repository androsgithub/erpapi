package com.api.erp.v1.main.features.endereco.infrastructure.validator;

import com.api.erp.v1.main.features.endereco.application.dto.request.CreateEnderecoRequest;
import com.api.erp.v1.main.features.endereco.domain.entity.EnderecoTipo;
import com.api.erp.v1.main.features.endereco.domain.validator.IEnderecoValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class EnderecoValidatorDefault implements IEnderecoValidator {

    @Override
    public void validarCriacao(CreateEnderecoRequest dto) {
        validarDTO(dto);
    }

    @Override
    public void validarAtualizacao(Long id, CreateEnderecoRequest dto) {
        validarId(id);
        validarDTO(dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        if (pageable == null) {
            throw new BusinessException("Paginação é obrigatória");
        }
        if (pageable.getPageSize() <= 0) {
            throw new BusinessException("Tamanho da página deve ser maior que zero");
        }
    }

    @Override
    public void validarDTO(CreateEnderecoRequest dto) {
        if (dto == null) {
            throw new BusinessException("DTO de endereço não pode ser nulo");
        }
        validarRua(dto.rua());
        validarNumero(dto.numero());
        validarBairro(dto.bairro());
        validarCidade(dto.cidade());
        validarEstado(dto.estado());
        validarCep(dto.cep());
        validarTipo(dto.tipo());
        validarPrincipal(dto.principal());
    }

    @Override
    public void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID do endereço é obrigatório e deve ser maior que zero");
        }
    }

    private void validarRua(String rua) {
        if (rua == null || rua.isBlank()) {
            throw new BusinessException("Rua é obrigatória");
        }
        if (rua.length() < 3) {
            throw new BusinessException("Rua deve ter no mínimo 3 caracteres");
        }
        if (rua.length() > 255) {
            throw new BusinessException("Rua não pode ter mais de 255 caracteres");
        }
    }

    private void validarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new BusinessException("Número é obrigatório");
        }
    }

    private void validarBairro(String bairro) {
        if (bairro == null || bairro.isBlank()) {
            throw new BusinessException("Bairro é obrigatório");
        }
        if (bairro.length() < 2) {
            throw new BusinessException("Bairro deve ter no mínimo 2 caracteres");
        }
    }

    private void validarCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            throw new BusinessException("Cidade é obrigatória");
        }
        if (cidade.length() < 2) {
            throw new BusinessException("Cidade deve ter no mínimo 2 caracteres");
        }
    }

    private void validarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new BusinessException("Estado é obrigatório");
        }
        if (estado.length() != 2) {
            throw new BusinessException("Estado deve ter exatamente 2 caracteres (UF)");
        }
    }

    private void validarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new BusinessException("CEP é obrigatório");
        }
        if (!cep.matches("\\d{5}-?\\d{3}")) {
            throw new BusinessException("CEP inválido. Use o formato: 12345-678 ou 12345678");
        }
    }

    private void validarTipo(EnderecoTipo tipo) {
        if (tipo == null) {
            throw new BusinessException("Tipo de endereço é obrigatório");
        }
    }

    private void validarPrincipal(Boolean principal) {
        if (principal == null) {
            throw new BusinessException("Campo 'principal' é obrigatório");
        }
    }
}
