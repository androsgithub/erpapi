package com.api.erp.features.produto.application.service;

import com.api.erp.features.produto.application.dto.*;
import com.api.erp.features.produto.domain.entity.Produto;
import com.api.erp.features.produto.domain.entity.StatusProduto;
import com.api.erp.features.produto.domain.entity.TipoProduto;
import com.api.erp.features.produto.domain.exception.ProdutoException;
import com.api.erp.features.produto.domain.repository.ProdutoRepository;
import com.api.erp.features.produto.domain.validator.ProdutoValidator;
import com.api.erp.features.unidademedida.domain.entity.UnidadeMedida;
import com.api.erp.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import com.api.erp.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de Aplicação para Produto
 * 
 * Responsabilidades:
 * - Orquestrar operações de domínio
 * - Coordenar transações
 * - Transformar DTOs
 * 
 * SRP: Lógica de aplicação para Produto
 * DIP: Depende de abstrações (repositórios, validadores)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProdutoService {
    
    private final ProdutoRepository repository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final ProdutoValidator validator;
    
    /**
     * Cria um novo produto
     */
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validator.validarCriacao(dto.getCodigo(), dto.getDescricao(), dto.getNcm());
        
        if (repository.existsByCodigo(dto.getCodigo())) {
            throw ProdutoException.codigoJaExiste(dto.getCodigo());
        }
        
        UnidadeMedida unidadeMedida = obterUnidadeMedida(dto.getUnidadeMedidaId());
        
        // Validar status padrão
        StatusProduto status = dto.getStatus() != null ? dto.getStatus() : StatusProduto.ATIVO;
        
        // Validar tipo padrão
        TipoProduto tipo = dto.getTipo() != null ? dto.getTipo() : TipoProduto.COMPRADO;
        
        Produto produto = Produto.builder()
                .codigo(dto.getCodigo().trim().toUpperCase())
                .descricao(dto.getDescricao().trim())
                .descricaoDetalhada(dto.getDescricaoDetalhada() != null ? dto.getDescricaoDetalhada().trim() : null)
                .status(status)
                .tipo(tipo)
                .unidadeMedida(unidadeMedida)
                .ncm(dto.getNcm().replaceAll("[^0-9]", ""))
                .informacoesFiscais(dto.getInformacoesFiscais() != null ? dto.getInformacoesFiscais().trim() : null)
                .precoVenda(dto.getPrecoVenda())
                .precoCusto(dto.getPrecoCusto())
                .build();
        
        Produto salvo = repository.save(produto);
        return converterParaResponseDTO(salvo);
    }
    
    /**
     * Atualiza um produto existente
     */
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = obterPorId(id);
        
        // Se o código foi alterado, validar unicidade
        if (!produto.getCodigo().equals(dto.getCodigo())) {
            if (repository.existsByCodigo(dto.getCodigo())) {
                throw ProdutoException.codigoJaExiste(dto.getCodigo());
            }
        }
        
        validator.validarCriacao(dto.getCodigo(), dto.getDescricao(), dto.getNcm());
        
        UnidadeMedida unidadeMedida = obterUnidadeMedida(dto.getUnidadeMedidaId());
        
        produto.setCodigo(dto.getCodigo().trim().toUpperCase());
        produto.setDescricao(dto.getDescricao().trim());
        produto.setDescricaoDetalhada(dto.getDescricaoDetalhada() != null ? dto.getDescricaoDetalhada().trim() : null);
        produto.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusProduto.ATIVO);
        produto.setTipo(dto.getTipo() != null ? dto.getTipo() : TipoProduto.COMPRADO);
        produto.setUnidadeMedida(unidadeMedida);
        produto.setNcm(dto.getNcm().replaceAll("[^0-9]", ""));
        produto.setInformacoesFiscais(dto.getInformacoesFiscais() != null ? dto.getInformacoesFiscais().trim() : null);
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setPrecoCusto(dto.getPrecoCusto());
        produto.atualizarDataAtualizacao();
        
        Produto atualizado = repository.save(produto);
        return converterParaResponseDTO(atualizado);
    }
    
    /**
     * Obtém um produto por ID
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO obter(Long id) {
        return converterParaResponseDTO(obterPorId(id));
    }
    
    /**
     * Lista todos os produtos (paginado)
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::converterParaResponseDTO);
    }
    
    /**
     * Lista produtos por tipo
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listarPorTipo(TipoProduto tipo, Pageable pageable) {
        return repository.findByTipo(tipo).stream()
                .map(this::converterParaResponseDTO)
                .reduce(
                    Page.empty(pageable),
                    (page, dto) -> page,
                    (p1, p2) -> p1
                );
    }
    
    /**
     * Ativa um produto
     */
    public ProdutoResponseDTO ativar(Long id) {
        Produto produto = obterPorId(id);
        produto.ativar();
        Produto atualizado = repository.save(produto);
        return converterParaResponseDTO(atualizado);
    }
    
    /**
     * Desativa um produto
     */
    public ProdutoResponseDTO desativar(Long id) {
        Produto produto = obterPorId(id);
        produto.desativar();
        Produto atualizado = repository.save(produto);
        return converterParaResponseDTO(atualizado);
    }
    
    /**
     * Bloqueia um produto
     */
    public ProdutoResponseDTO bloquear(Long id) {
        Produto produto = obterPorId(id);
        produto.bloquear();
        Produto atualizado = repository.save(produto);
        return converterParaResponseDTO(atualizado);
    }
    
    /**
     * Descontinua um produto
     */
    public ProdutoResponseDTO descontinuar(Long id) {
        Produto produto = obterPorId(id);
        produto.descontinuar();
        Produto atualizado = repository.save(produto);
        return converterParaResponseDTO(atualizado);
    }
    
    /**
     * Deleta um produto
     */
    public void deletar(Long id) {
        obterPorId(id); // Valida existência
        repository.deleteById(id);
    }
    
    /**
     * Obtém um produto por ID ou lança exceção
     */
    private Produto obterPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ProdutoException.produtoNaoEncontrado(id));
    }
    
    /**
     * Obtém unidade de medida ou lança exceção
     */
    private UnidadeMedida obterUnidadeMedida(Long id) {
        return unidadeMedidaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "Unidade de medida não encontrada com ID: " + id
                ));
    }
    
    /**
     * Converte entidade para DTO
     */
    private ProdutoResponseDTO converterParaResponseDTO(Produto produto) {
        return ProdutoResponseDTO.builder()
                .id(produto.getId())
                .codigo(produto.getCodigo())
                .descricao(produto.getDescricao())
                .descricaoDetalhada(produto.getDescricaoDetalhada())
                .status(produto.getStatus())
                .tipo(produto.getTipo())
                .unidadeMedida(
                    ProdutoResponseDTO.UnidadeMedidaSimplificadaDTO.builder()
                        .id(produto.getUnidadeMedida().getId())
                        .sigla(produto.getUnidadeMedida().getSigla())
                        .descricao(produto.getUnidadeMedida().getDescricao())
                        .build()
                )
                .ncm(produto.getNcm())
                .informacoesFiscais(produto.getInformacoesFiscais())
                .precoVenda(produto.getPrecoVenda())
                .precoCusto(produto.getPrecoCusto())
                .dataCriacao(produto.getDataCriacao())
                .dataAtualizacao(produto.getDataAtualizacao())
                .build();
    }
}
