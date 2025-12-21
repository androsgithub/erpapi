package com.api.erp.features.produto.domain.validator;

import com.api.erp.features.produto.domain.entity.Produto;
import com.api.erp.features.produto.domain.entity.ProdutoComposicao;
import com.api.erp.features.produto.domain.exception.ProdutoException;
import com.api.erp.features.produto.domain.repository.ProdutoComposicaoRepository;
import com.api.erp.shared.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Validador de Produto
 * 
 * SRP: Responsável única por validações de domínio
 */
@Component
@RequiredArgsConstructor
public class ProdutoValidator {
    
    private final ProdutoComposicaoRepository composicaoRepository;
    
    /**
     * Valida os dados básicos de um produto
     */
    public void validarCriacao(String codigo, String descricao, String ncm) {
        validarCodigo(codigo);
        validarDescricao(descricao);
        validarNCM(ncm);
    }
    
    /**
     * Valida o código do produto
     */
    public void validarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ValidationException("codigo", "O código do produto é obrigatório");
        }
        
        if (codigo.length() > 50) {
            throw new ValidationException("codigo", "O código não pode ter mais de 50 caracteres");
        }
        
        if (!codigo.matches("^[A-Z0-9-._]+$")) {
            throw new ValidationException(
                "codigo",
                "O código deve conter apenas letras maiúsculas, números, hífen, ponto e underscore"
            );
        }
    }
    
    /**
     * Valida a descrição
     */
    public void validarDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new ValidationException("descricao", "A descrição do produto é obrigatória");
        }
        
        if (descricao.length() > 255) {
            throw new ValidationException(
                "descricao",
                "A descrição não pode ter mais de 255 caracteres"
            );
        }
    }
    
    /**
     * Valida o NCM
     */
    public void validarNCM(String ncm) {
        if (ncm == null || ncm.trim().isEmpty()) {
            throw new ValidationException("ncm", "O NCM é obrigatório");
        }
        
        String ncmLimpo = ncm.replaceAll("[^0-9]", "");
        
        if (ncmLimpo.length() != 8) {
            throw new ValidationException(
                "ncm",
                "O NCM deve conter exatamente 8 dígitos"
            );
        }
        
        if (!ncmLimpo.matches("^[0-9]{8}$")) {
            throw new ValidationException(
                "ncm",
                "O NCM deve conter apenas números"
            );
        }
    }
    
    /**
     * Valida uma composição de produto
     */
    public void validarComposicao(Produto produtoFabricado, Produto componente, BigDecimal quantidade) {
        // Validar se produto é fabricável
        if (!produtoFabricado.ehFabricavel()) {
            throw ProdutoException.produtoNaoFabricavel(produtoFabricado.getId());
        }
        
        // Validar quantidade
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw ProdutoException.quantidadeInvalida();
        }
        
        // Validar se não há composição circular
        validarComposicaoCircular(produtoFabricado, componente);
        
        // Validar se componente está ativo
        if (!componente.podeSerUtilizado()) {
            throw ProdutoException.produtoNaoPodeSerUtilizado(componente.getId());
        }
    }
    
    /**
     * Valida se não há composição circular
     * Exemplo: Se Produto A usa Produto B, então Produto B não pode usar Produto A
     */
    private void validarComposicaoCircular(Produto produtoFabricado, Produto componente) {
        // Se o componente é igual ao produto fabricado, é circular
        if (produtoFabricado.getId().equals(componente.getId())) {
            throw ProdutoException.composicaoCircular(
                produtoFabricado.getCodigo(),
                componente.getCodigo()
            );
        }
        
        // Verificar se o componente usa o produtoFabricado em sua composição
        // Isso previne composições circulares simples
        if (ehComponenteDe(componente, produtoFabricado)) {
            throw ProdutoException.composicaoCircular(
                produtoFabricado.getCodigo(),
                componente.getCodigo()
            );
        }
    }
    
    /**
     * Verifica se um produto é componente de outro (busca simples)
     */
    private boolean ehComponenteDe(Produto possibleComponente, Produto possibleFabricado) {
        var composicoes = composicaoRepository.findByProdutoFabricadoId(possibleComponente.getId());
        
        for (ProdutoComposicao comp : composicoes) {
            if (comp.getProdutoComponente().getId().equals(possibleFabricado.getId())) {
                return true;
            }
        }
        
        return false;
    }
}
