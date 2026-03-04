package com.api.erp.v1.main.features.measureunit.infrastructure.service;

import com.api.erp.v1.main.features.measureunit.application.dto.request.MeasureUnitRequestDTO;
import com.api.erp.v1.main.features.measureunit.application.dto.response.MeasureUnitResponseDTO;
import com.api.erp.v1.main.features.measureunit.domain.entity.MeasureUnit;
import com.api.erp.v1.main.features.measureunit.domain.repository.MeasureUnitRepository;
import com.api.erp.v1.main.features.measureunit.domain.service.IMeasureUnitService;
import com.api.erp.v1.main.features.measureunit.domain.validator.MeasureUnitValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação para MeasureUnit
 * <p>
 * Responsibilities:
 * - Orquestrar operações de domínio
 * - Coordenar transações
 * - Transformar DTOs
 * <p>
 * SRP: Lógica de aplicação para MeasureUnit
 * DIP: Depende de abstrações (repositório, validador)
 */
@RequiredArgsConstructor
@Transactional
public class MeasureUnitService implements IMeasureUnitService {

    private final MeasureUnitRepository repository;
    private final MeasureUnitValidator validator;

    /**
     * Cria uma nova unidade de medida
     */
    public MeasureUnitResponseDTO criar(MeasureUnitRequestDTO dto) {
        validator.validarCriacao(dto.getSigla(), dto.getDescricao());

        if (repository.existsBySigla(dto.getSigla())) {
            throw new BusinessException(HttpStatus.CONFLICT, "A unit of measurement with code already exists: " + dto.getSigla());
        }

        MeasureUnit unidade = MeasureUnit.builder().sigla(dto.getSigla().trim().toUpperCase()).descricao(dto.getDescricao().trim()).build();

        MeasureUnit salva = repository.save(unidade);
        return converterParaResponseDTO(salva);
    }

    /**
     * Atualiza uma unidade de medida
     */
    public MeasureUnitResponseDTO atualizar(Long id, MeasureUnitRequestDTO dto) {
        MeasureUnit unidade = obterPorId(id);

        if (!unidade.getSigla().equals(dto.getSigla())) {
            if (repository.existsBySigla(dto.getSigla())) {
                throw new BusinessException(HttpStatus.CONFLICT, "A unit of measurement with code already exists: " + dto.getSigla());
            }
        }

        validator.validarCriacao(dto.getSigla(), dto.getDescricao());

        unidade.setSigla(dto.getSigla().trim().toUpperCase());
        unidade.setDescricao(dto.getDescricao().trim());

        MeasureUnit atualizada = repository.save(unidade);
        return converterParaResponseDTO(atualizada);
    }

    /**
     * Gets uma unidade de medida por ID
     */
    @Transactional(readOnly = true)
    public MeasureUnitResponseDTO obter(Long id) {
        return converterParaResponseDTO(obterPorId(id));
    }

    /**
     * Lista todas as unidades de medida (paginada)
     */
    @Transactional(readOnly = true)
    public Page<MeasureUnitResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable).map(this::converterParaResponseDTO);
    }

    /**
     * Ativa uma unidade de medida
     */
    public MeasureUnitResponseDTO ativar(Long id) {
        MeasureUnit unidade = obterPorId(id);
        MeasureUnit atualizada = repository.save(unidade);
        return converterParaResponseDTO(atualizada);
    }

    /**
     * Desativa uma unidade de medida
     */
    public MeasureUnitResponseDTO desativar(Long id) {
        MeasureUnit unidade = obterPorId(id);
        MeasureUnit atualizada = repository.save(unidade);
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
     * Gets uma unidade por ID ou lança exceção
     */
    private MeasureUnit obterPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Unit of measurement not found with ID: " + id));
    }

    /**
     * Converte entidade para DTO
     */
    private MeasureUnitResponseDTO converterParaResponseDTO(MeasureUnit unidade) {
        return MeasureUnitResponseDTO.builder().id(unidade.getId()).sigla(unidade.getSigla()).descricao(unidade.getDescricao()).build();
    }
}
