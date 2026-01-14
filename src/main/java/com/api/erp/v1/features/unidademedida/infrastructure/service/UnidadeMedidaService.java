package com.api.erp.v1.features.unidademedida.infrastructure.service;

import com.api.erp.v1.features.unidademedida.application.dto.request.UnidadeMedidaRequestDTO;
import com.api.erp.v1.features.unidademedida.application.dto.response.UnidadeMedidaResponseDTO;
import com.api.erp.v1.features.unidademedida.domain.entity.UnidadeMedida;
import com.api.erp.v1.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import com.api.erp.v1.features.unidademedida.domain.service.IUnidadeMedidaService;
import com.api.erp.v1.features.unidademedida.domain.validator.UnidadeMedidaValidator;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação para UnidadeMedida
 * <p>
 * Responsabilidades:
 * - Orquestrar operações de domínio
 * - Coordenar transações
 * - Transformar DTOs
 * <p>
 * SRP: Lógica de aplicação para UnidadeMedida
 * DIP: Depende de abstrações (repositório, validador)
 */
@RequiredArgsConstructor
@Transactional
public class UnidadeMedidaService implements IUnidadeMedidaService {

    private final UnidadeMedidaRepository repository;
    private final UnidadeMedidaValidator validator;

    /**
     * Cria uma nova unidade de medida
     */
    public UnidadeMedidaResponseDTO criar(UnidadeMedidaRequestDTO dto) {
        validator.validarCriacao(dto.getSigla(), dto.getDescricao());

        if (repository.existsBySigla(dto.getSigla())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Já existe uma unidade de medida com a sigla: " + dto.getSigla());
        }

        UnidadeMedida unidade = UnidadeMedida.builder().sigla(dto.getSigla().trim().toUpperCase()).descricao(dto.getDescricao().trim()).ativo(true).build();

        UnidadeMedida salva = repository.save(unidade);
        return converterParaResponseDTO(salva);
    }

    /**
     * Atualiza uma unidade de medida
     */
    public UnidadeMedidaResponseDTO atualizar(Long id, UnidadeMedidaRequestDTO dto) {
        UnidadeMedida unidade = obterPorId(id);

        if (!unidade.getSigla().equals(dto.getSigla())) {
            if (repository.existsBySigla(dto.getSigla())) {
                throw new BusinessException(HttpStatus.CONFLICT, "Já existe uma unidade de medida com a sigla: " + dto.getSigla());
            }
        }

        validator.validarCriacao(dto.getSigla(), dto.getDescricao());

        unidade.setSigla(dto.getSigla().trim().toUpperCase());
        unidade.setDescricao(dto.getDescricao().trim());
        unidade.atualizarDataAtualizacao();

        UnidadeMedida atualizada = repository.save(unidade);
        return converterParaResponseDTO(atualizada);
    }

    /**
     * Obtém uma unidade de medida por ID
     */
    @Transactional(readOnly = true)
    public UnidadeMedidaResponseDTO obter(Long id) {
        return converterParaResponseDTO(obterPorId(id));
    }

    /**
     * Lista todas as unidades de medida (paginada)
     */
    @Transactional(readOnly = true)
    public Page<UnidadeMedidaResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable).map(this::converterParaResponseDTO);
    }

    /**
     * Ativa uma unidade de medida
     */
    public UnidadeMedidaResponseDTO ativar(Long id) {
        UnidadeMedida unidade = obterPorId(id);
        unidade.ativar();
        UnidadeMedida atualizada = repository.save(unidade);
        return converterParaResponseDTO(atualizada);
    }

    /**
     * Desativa uma unidade de medida
     */
    public UnidadeMedidaResponseDTO desativar(Long id) {
        UnidadeMedida unidade = obterPorId(id);
        unidade.desativar();
        UnidadeMedida atualizada = repository.save(unidade);
        return converterParaResponseDTO(atualizada);
    }

    /**
     * Deleta uma unidade de medida
     */
    public void deletar(Long id) {
        obterPorId(id); // Valida existência
        repository.deleteById(id);
    }

    /**
     * Obtém uma unidade por ID ou lança exceção
     */
    private UnidadeMedida obterPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Unidade de medida não encontrada com ID: " + id));
    }

    /**
     * Converte entidade para DTO
     */
    private UnidadeMedidaResponseDTO converterParaResponseDTO(UnidadeMedida unidade) {
        return UnidadeMedidaResponseDTO.builder().id(unidade.getId()).sigla(unidade.getSigla()).descricao(unidade.getDescricao()).ativo(unidade.getAtivo()).dataCriacao(unidade.getDataCriacao()).dataAtualizacao(unidade.getDataAtualizacao()).build();
    }
}
