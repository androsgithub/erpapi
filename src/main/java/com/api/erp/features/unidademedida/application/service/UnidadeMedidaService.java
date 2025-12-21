package com.api.erp.features.unidademedida.application.service;

import com.api.erp.features.unidademedida.application.dto.UnidadeMedidaRequestDTO;
import com.api.erp.features.unidademedida.application.dto.UnidadeMedidaResponseDTO;
import com.api.erp.features.unidademedida.domain.entity.UnidadeMedida;
import com.api.erp.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import com.api.erp.features.unidademedida.domain.validator.UnidadeMedidaValidator;
import com.api.erp.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de aplicação para UnidadeMedida
 * 
 * Responsabilidades:
 * - Orquestrar operações de domínio
 * - Coordenar transações
 * - Transformar DTOs
 * 
 * SRP: Lógica de aplicação para UnidadeMedida
 * DIP: Depende de abstrações (repositório, validador)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UnidadeMedidaService {
    
    private final UnidadeMedidaRepository repository;
    private final UnidadeMedidaValidator validator;
    
    /**
     * Cria uma nova unidade de medida
     */
    public UnidadeMedidaResponseDTO criar(UnidadeMedidaRequestDTO dto) {
        validator.validarCriacao(dto.getSigla(), dto.getDescricao(), dto.getTipo());
        
        if (repository.existsBySigla(dto.getSigla())) {
            throw new BusinessException(
                HttpStatus.CONFLICT,
                "Já existe uma unidade de medida com a sigla: " + dto.getSigla()
            );
        }
        
        UnidadeMedida unidade = UnidadeMedida.builder()
                .sigla(dto.getSigla().trim().toUpperCase())
                .descricao(dto.getDescricao().trim())
                .tipo(dto.getTipo() != null ? dto.getTipo().trim() : null)
                .ativo(true)
                .build();
        
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
                throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "Já existe uma unidade de medida com a sigla: " + dto.getSigla()
                );
            }
        }
        
        validator.validarCriacao(dto.getSigla(), dto.getDescricao(), dto.getTipo());
        
        unidade.setSigla(dto.getSigla().trim().toUpperCase());
        unidade.setDescricao(dto.getDescricao().trim());
        unidade.setTipo(dto.getTipo() != null ? dto.getTipo().trim() : null);
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
        return repository.findAll(pageable)
                .map(this::converterParaResponseDTO);
    }
    
    /**
     * Lista todas as unidades de medida ativas
     */
    @Transactional(readOnly = true)
    public List<UnidadeMedidaResponseDTO> listarAtivas() {
        return repository.findByAtivoTrue().stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
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
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "Unidade de medida não encontrada com ID: " + id
                ));
    }
    
    /**
     * Converte entidade para DTO
     */
    private UnidadeMedidaResponseDTO converterParaResponseDTO(UnidadeMedida unidade) {
        return UnidadeMedidaResponseDTO.builder()
                .id(unidade.getId())
                .sigla(unidade.getSigla())
                .descricao(unidade.getDescricao())
                .tipo(unidade.getTipo())
                .ativo(unidade.getAtivo())
                .dataCriacao(unidade.getDataCriacao())
                .dataAtualizacao(unidade.getDataAtualizacao())
                .build();
    }
}
