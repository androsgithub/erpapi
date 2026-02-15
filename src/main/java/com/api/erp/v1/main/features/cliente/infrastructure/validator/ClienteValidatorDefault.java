package com.api.erp.v1.main.features.cliente.infrastructure.validator;

import com.api.erp.v1.main.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.main.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ClienteValidatorDefault implements IClienteValidator {

    @Override
    public void validarCriacao(CreateClienteDto dto) {
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }

        if (dto.nome().length() < 3) {
            throw new IllegalArgumentException("Nome deve ter no mínimo 3 caracteres");
        }
    }

    @Override
    public void validarAtualizacao(Long id, CreateClienteDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }
        validarCriacao(dto);
    }

    @Override
    public void validarPageable(Pageable pageable) {
        if (pageable == null) {
            throw new BusinessException("Configuração de paginação para cliente não encontrada");
        }
    }

    @Override
    public void validarDTO(CreateClienteDto clienteDto) {
        if (clienteDto == null) {
            throw new BusinessException("CreateClienteDto não pode ser nulo");
        }
        if (clienteDto.nome() == null || clienteDto.nome().isBlank()) {
            throw new BusinessException("Nome do cliente é obrigatório");
        }
        if (clienteDto.dadosFiscais() == null) {
            throw new BusinessException("Dados fiscais são obrigatórios");
        }

        // Validações de dados fiscais
        var df = clienteDto.dadosFiscais();
        if (df.cnpj() == null || df.cnpj().isBlank()) {
            throw new BusinessException("CNPJ é obrigatório");
        }
        if (df.razaoSocial() == null || df.razaoSocial().isBlank()) {
            throw new BusinessException("Razão social é obrigatória");
        }
    }

    @Override
    public void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID do cliente inválido");
        }
    }
}
