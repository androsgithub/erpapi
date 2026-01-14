package com.api.erp.v1.features.produto.infrastructure.service;

import com.api.erp.v1.features.produto.application.dto.ClassificacaoFiscalDTO;
import com.api.erp.v1.features.produto.application.dto.ProdutoRequestDTO;
import com.api.erp.v1.features.produto.domain.entity.ClassificacaoFiscal;
import com.api.erp.v1.features.produto.domain.entity.Produto;
import com.api.erp.v1.features.produto.domain.entity.TipoProduto;
import com.api.erp.v1.features.produto.domain.exception.ProdutoException;
import com.api.erp.v1.features.produto.domain.repository.ProdutoRepository;
import com.api.erp.v1.features.produto.domain.service.IProdutoService;
import com.api.erp.v1.features.produto.domain.validator.ProdutoValidator;
import com.api.erp.v1.features.unidademedida.domain.entity.UnidadeMedida;
import com.api.erp.v1.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import com.api.erp.v1.shared.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de Aplicação para Produto
 * <p>
 * Responsabilidades:
 * - Orquestrar operações de domínio
 * - Coordenar transações
 * - Transformar DTOs
 * <p>
 * SRP: Lógica de aplicação para Produto
 * DIP: Depende de abstrações (repositórios, validadores)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProdutoService implements IProdutoService {

    private final ProdutoRepository repository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final ProdutoValidator validator;

    public Produto criar(ProdutoRequestDTO dto) {
        validator.validarCriacao(dto.getCodigo(), dto.getDescricao());

        if (repository.existsByCodigo(dto.getCodigo())) {
            throw ProdutoException.codigoJaExiste(dto.getCodigo());
        }

        Produto produto = new Produto();
        produto.setCodigo(dto.getCodigo());
        produto.setDescricao(dto.getDescricao());
        produto.setStatus(dto.getStatus());
        produto.setTipo(dto.getTipo());

        UnidadeMedida unidadeMedida = obterUnidadeMedida(dto.getUnidadeMedidaId());
        produto.setUnidadeMedida(unidadeMedida);
        produto.setClassificacaoFiscal(criarClassificacaoFiscal(dto.getClassificacaoFiscal()));
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setPrecoCusto(dto.getPrecoCusto());
        produto.setDescricaoDetalhada(dto.getDescricaoDetalhada());
        produto.setCustomData(dto.getCustomData());


        return repository.save(produto);
    }

    public Produto atualizar(Long id, ProdutoRequestDTO produtoModificado) {
        Produto produto = obterPorId(id);

        // Se o código foi alterado, validar unicidade
        if (!produto.getCodigo().equals(produtoModificado.getCodigo())) {
            if (repository.existsByCodigo(produtoModificado.getCodigo())) {
                throw ProdutoException.codigoJaExiste(produtoModificado.getCodigo());
            }
        }

        validator.validarCriacao(produtoModificado.getCodigo(), produtoModificado.getDescricao());
        return repository.save(produto);
    }

    /**
     * Obtém um produto por ID
     */
    @Transactional(readOnly = true)
    public Produto obter(Long id) {
        return obterPorId(id);
    }

    /**
     * Lista todos os produtos (paginado)
     */
    @Transactional(readOnly = true)
    public Page<Produto> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Lista produtos por tipo
     */
    @Transactional(readOnly = true)
    public Page<Produto> listarPorTipo(TipoProduto tipo, Pageable pageable) {
        return repository.findByTipo(tipo).stream()
                .reduce(
                        Page.empty(pageable),
                        (page, dto) -> page,
                        (p1, p2) -> p1
                );
    }

    /**
     * Ativa um produto
     */
    public Produto ativar(Long id) {
        Produto produto = obterPorId(id);
        produto.ativar();
        return repository.save(produto);
    }

    /**
     * Desativa um produto
     */
    public Produto desativar(Long id) {
        Produto produto = obterPorId(id);
        produto.desativar();
        return repository.save(produto);
    }

    /**
     * Bloqueia um produto
     */
    public Produto bloquear(Long id) {
        Produto produto = obterPorId(id);
        produto.bloquear();
        return repository.save(produto);
    }

    /**
     * Descontinua um produto
     */
    public Produto descontinuar(Long id) {
        Produto produto = obterPorId(id);
        produto.descontinuar();
        return repository.save(produto);
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
     * Cria uma ClassificacaoFiscal a partir do DTO
     */
    private ClassificacaoFiscal criarClassificacaoFiscal(ClassificacaoFiscalDTO dto) {
        NCM ncm = NCM.de(dto.getNcm());
        OrigemMercadoria origem = OrigemMercadoria.doCodigo(dto.getOrigemMercadoria());
        CodigoBeneficioFiscal beneficio = dto.getCodigoBeneficioFiscal() != null
                ? CodigoBeneficioFiscal.de(dto.getCodigoBeneficioFiscal())
                : null;
        CEST cest = dto.getCest() != null
                ? CEST.de(dto.getCest())
                : null;
        UnidadeTributavel unidadeTributavel = UnidadeTributavel.de(
                dto.getUnidadeTributavelCodigo(),
                dto.getUnidadeTributavelDescricao()
        );

        return ClassificacaoFiscal.criar(
                ncm,
                dto.getDescricaoFiscal(),
                origem,
                beneficio,
                cest,
                unidadeTributavel
        );
    }

}
