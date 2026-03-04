package com.api.erp.v1.main.features.product.infrastructure.service;

import com.api.erp.v1.main.features.product.application.dto.CompositionRequestDTO;
import com.api.erp.v1.main.features.product.application.dto.CompositionResponseDTO;
import com.api.erp.v1.main.features.product.domain.entity.Product;
import com.api.erp.v1.main.features.product.domain.entity.ProductComposition;
import com.api.erp.v1.main.features.product.domain.exception.ProductException;
import com.api.erp.v1.main.features.product.domain.repository.ProductCompositionRepository;
import com.api.erp.v1.main.features.product.domain.repository.ProductRepository;
import com.api.erp.v1.main.features.product.domain.service.ICompositionService;
import com.api.erp.v1.main.features.product.domain.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para Composição de Product (BOM)
 * 
 * Responsibilities:
 * - Managesr composições de products fabricáveis
 * - Validate regras de domínio (composição circular, quantidade, etc)
 * - Transformar DTOs
 * 
 * SRP: Lógica de aplicação para composições
 * DIP: Depende de abstrações (repositórios, validadores)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CompositionService implements ICompositionService {
    
    private final ProductCompositionRepository compositionRepository;
    private final ProductRepository productRepository;
    private final ProductValidator validator;
    
    /**
     * Cria uma nova composição de product
     */
    public CompositionResponseDTO criar(CompositionRequestDTO dto) {
        Product productFabricado = obterProduct(dto.getProductFabricadoId());
        Product productComponente = obterProduct(dto.getProductComponenteId());
        
        // Validate composição
        validator.validarComposition(productFabricado, productComponente, dto.getQuantidadeNecessaria());
        
        // Check se já existe essa composição
        var compositionExistente = compositionRepository.findByProductFabricadoIdAndProductComponenteId(
                dto.getProductFabricadoId(),
                dto.getProductComponenteId()
        );
        
        if (compositionExistente.isPresent()) {
            throw new ProductException(
                HttpStatus.CONFLICT,
                "Já existe uma composição entre estes products"
            );
        }
        
        ProductComposition composition = ProductComposition.builder()
                .productFabricado(productFabricado)
                .productComponente(productComponente)
                .quantidadeNecessaria(dto.getQuantidadeNecessaria())
                .sequencia(dto.getSequencia() != null ? dto.getSequencia() : 0)
                .observacoes(dto.getObservacoes())
                .build();
        
        ProductComposition salva = compositionRepository.save(composition);
        return converterParaResponseDTO(salva);
    }
    
    /**
     * Atualiza uma composição existente
     */
    public CompositionResponseDTO atualizar(Long id, CompositionRequestDTO dto) {
        ProductComposition composition = obterPorId(id);
        
        // Se o componente foi alterado, validar nova composição
        if (!composition.getProductComponente().getId().equals(dto.getProductComponenteId())) {
            Product novoComponente = obterProduct(dto.getProductComponenteId());
            validator.validarComposition(
                    composition.getProductFabricado(),
                    novoComponente,
                    dto.getQuantidadeNecessaria()
            );
            composition.setProductComponente(novoComponente);
        } else {
            // Validate apenas a quantidade
            if (dto.getQuantidadeNecessaria() == null || 
                    dto.getQuantidadeNecessaria().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw ProductException.quantidadeInvalida();
            }
        }
        
        composition.atualizarQuantidade(dto.getQuantidadeNecessaria());
        composition.setSequencia(dto.getSequencia() != null ? dto.getSequencia() : composition.getSequencia());
        composition.setObservacoes(dto.getObservacoes());
        composition.atualizarDataAtualizacao();
        
        ProductComposition atualizada = compositionRepository.save(composition);
        return converterParaResponseDTO(atualizada);
    }
    
    /**
     * Gets uma composição por ID
     */
    @Transactional(readOnly = true)
    public CompositionResponseDTO obter(Long id) {
        return converterParaResponseDTO(obterPorId(id));
    }
    
    /**
     * Lista composições de um product fabricado
     */
    @Transactional(readOnly = true)
    public List<CompositionResponseDTO> listarComposicoesPor(Long productFabricadoId) {
        // Validate que o product existe
        obterProduct(productFabricadoId);
        
        return compositionRepository.findByProductFabricadoId(productFabricadoId).stream()
                .sorted((a, b) -> a.getSequencia().compareTo(b.getSequencia()))
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Deleta uma composição
     */
    public void deletar(Long id) {
        obterPorId(id);
        compositionRepository.deleteById(id);
    }
    
    /**
     * Deleta todas as composições de um product
     */
    public void deletarComposicoesDeProduct(Long productFabricadoId) {
        obterProduct(productFabricadoId);
        compositionRepository.deleteByProductFabricadoId(productFabricadoId);
    }
    
    /**
     * Gets composição por ID ou lança exceção
     */
    private ProductComposition obterPorId(Long id) {
        return compositionRepository.findById(id)
                .orElseThrow(() -> new ProductException(
                    HttpStatus.NOT_FOUND,
                    "Composição não encontrada com ID: " + id
                ));
    }
    
    /**
     * Gets product por ID ou lança exceção
     */
    private Product obterProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ProductException.productNaoEncontrado(id));
    }
    
    /**
     * Converte entidade para DTO
     */
    private CompositionResponseDTO converterParaResponseDTO(ProductComposition composition) {
        return CompositionResponseDTO.builder()
                .id(composition.getId())
                .productFabricado(
                    CompositionResponseDTO.ProductSimplificadoDTO.builder()
                        .id(composition.getProductFabricado().getId())
                        .codigo(composition.getProductFabricado().getCodigo())
                        .descricao(composition.getProductFabricado().getDescricao())
                        .build()
                )
                .productComponente(
                    CompositionResponseDTO.ProductSimplificadoDTO.builder()
                        .id(composition.getProductComponente().getId())
                        .codigo(composition.getProductComponente().getCodigo())
                        .descricao(composition.getProductComponente().getDescricao())
                        .build()
                )
                .quantidadeNecessaria(composition.getQuantidadeNecessaria())
                .sequencia(composition.getSequencia())
                .observacoes(composition.getObservacoes())
                .dataCriacao(composition.getDataCriacao())
                .dataAtualizacao(composition.getDataAtualizacao())
                .build();
    }
}
