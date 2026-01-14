package com.api.erp.v1.features.produto.infrastructure.service;

import com.api.erp.v1.features.produto.application.dto.ComposicaoRequestDTO;
import com.api.erp.v1.features.produto.application.dto.ComposicaoResponseDTO;
import com.api.erp.v1.features.produto.domain.entity.Produto;
import com.api.erp.v1.features.produto.domain.entity.ProdutoComposicao;
import com.api.erp.v1.features.produto.domain.exception.ProdutoException;
import com.api.erp.v1.features.produto.domain.repository.ProdutoComposicaoRepository;
import com.api.erp.v1.features.produto.domain.repository.ProdutoRepository;
import com.api.erp.v1.features.produto.domain.service.IComposicaoService;
import com.api.erp.v1.features.produto.domain.validator.ProdutoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para Composição de Produto (BOM)
 * 
 * Responsabilidades:
 * - Gerenciar composições de produtos fabricáveis
 * - Validar regras de domínio (composição circular, quantidade, etc)
 * - Transformar DTOs
 * 
 * SRP: Lógica de aplicação para composições
 * DIP: Depende de abstrações (repositórios, validadores)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ComposicaoService implements IComposicaoService {
    
    private final ProdutoComposicaoRepository composicaoRepository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoValidator validator;
    
    /**
     * Cria uma nova composição de produto
     */
    public ComposicaoResponseDTO criar(ComposicaoRequestDTO dto) {
        Produto produtoFabricado = obterProduto(dto.getProdutoFabricadoId());
        Produto produtoComponente = obterProduto(dto.getProdutoComponenteId());
        
        // Validar composição
        validator.validarComposicao(produtoFabricado, produtoComponente, dto.getQuantidadeNecessaria());
        
        // Verificar se já existe essa composição
        var composicaoExistente = composicaoRepository.findByProdutoFabricadoIdAndProdutoComponenteId(
                dto.getProdutoFabricadoId(),
                dto.getProdutoComponenteId()
        );
        
        if (composicaoExistente.isPresent()) {
            throw new ProdutoException(
                HttpStatus.CONFLICT,
                "Já existe uma composição entre estes produtos"
            );
        }
        
        ProdutoComposicao composicao = ProdutoComposicao.builder()
                .produtoFabricado(produtoFabricado)
                .produtoComponente(produtoComponente)
                .quantidadeNecessaria(dto.getQuantidadeNecessaria())
                .sequencia(dto.getSequencia() != null ? dto.getSequencia() : 0)
                .observacoes(dto.getObservacoes())
                .build();
        
        ProdutoComposicao salva = composicaoRepository.save(composicao);
        return converterParaResponseDTO(salva);
    }
    
    /**
     * Atualiza uma composição existente
     */
    public ComposicaoResponseDTO atualizar(Long id, ComposicaoRequestDTO dto) {
        ProdutoComposicao composicao = obterPorId(id);
        
        // Se o componente foi alterado, validar nova composição
        if (!composicao.getProdutoComponente().getId().equals(dto.getProdutoComponenteId())) {
            Produto novoComponente = obterProduto(dto.getProdutoComponenteId());
            validator.validarComposicao(
                    composicao.getProdutoFabricado(),
                    novoComponente,
                    dto.getQuantidadeNecessaria()
            );
            composicao.setProdutoComponente(novoComponente);
        } else {
            // Validar apenas a quantidade
            if (dto.getQuantidadeNecessaria() == null || 
                    dto.getQuantidadeNecessaria().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw ProdutoException.quantidadeInvalida();
            }
        }
        
        composicao.atualizarQuantidade(dto.getQuantidadeNecessaria());
        composicao.setSequencia(dto.getSequencia() != null ? dto.getSequencia() : composicao.getSequencia());
        composicao.setObservacoes(dto.getObservacoes());
        composicao.atualizarDataAtualizacao();
        
        ProdutoComposicao atualizada = composicaoRepository.save(composicao);
        return converterParaResponseDTO(atualizada);
    }
    
    /**
     * Obtém uma composição por ID
     */
    @Transactional(readOnly = true)
    public ComposicaoResponseDTO obter(Long id) {
        return converterParaResponseDTO(obterPorId(id));
    }
    
    /**
     * Lista composições de um produto fabricado
     */
    @Transactional(readOnly = true)
    public List<ComposicaoResponseDTO> listarComposicoesPor(Long produtoFabricadoId) {
        // Validar que o produto existe
        obterProduto(produtoFabricadoId);
        
        return composicaoRepository.findByProdutoFabricadoId(produtoFabricadoId).stream()
                .sorted((a, b) -> a.getSequencia().compareTo(b.getSequencia()))
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Deleta uma composição
     */
    public void deletar(Long id) {
        obterPorId(id);
        composicaoRepository.deleteById(id);
    }
    
    /**
     * Deleta todas as composições de um produto
     */
    public void deletarComposicoesDeProduto(Long produtoFabricadoId) {
        obterProduto(produtoFabricadoId);
        composicaoRepository.deleteByProdutoFabricadoId(produtoFabricadoId);
    }
    
    /**
     * Obtém composição por ID ou lança exceção
     */
    private ProdutoComposicao obterPorId(Long id) {
        return composicaoRepository.findById(id)
                .orElseThrow(() -> new ProdutoException(
                    HttpStatus.NOT_FOUND,
                    "Composição não encontrada com ID: " + id
                ));
    }
    
    /**
     * Obtém produto por ID ou lança exceção
     */
    private Produto obterProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> ProdutoException.produtoNaoEncontrado(id));
    }
    
    /**
     * Converte entidade para DTO
     */
    private ComposicaoResponseDTO converterParaResponseDTO(ProdutoComposicao composicao) {
        return ComposicaoResponseDTO.builder()
                .id(composicao.getId())
                .produtoFabricado(
                    ComposicaoResponseDTO.ProdutoSimplificadoDTO.builder()
                        .id(composicao.getProdutoFabricado().getId())
                        .codigo(composicao.getProdutoFabricado().getCodigo())
                        .descricao(composicao.getProdutoFabricado().getDescricao())
                        .build()
                )
                .produtoComponente(
                    ComposicaoResponseDTO.ProdutoSimplificadoDTO.builder()
                        .id(composicao.getProdutoComponente().getId())
                        .codigo(composicao.getProdutoComponente().getCodigo())
                        .descricao(composicao.getProdutoComponente().getDescricao())
                        .build()
                )
                .quantidadeNecessaria(composicao.getQuantidadeNecessaria())
                .sequencia(composicao.getSequencia())
                .observacoes(composicao.getObservacoes())
                .dataCriacao(composicao.getDataCriacao())
                .dataAtualizacao(composicao.getDataAtualizacao())
                .build();
    }
}
