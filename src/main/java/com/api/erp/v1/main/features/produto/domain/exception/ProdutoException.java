package com.api.erp.v1.main.features.produto.domain.exception;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando há violação de regra de negócio relacionada a produtos
 */
public class ProdutoException extends BusinessException {
    
    public ProdutoException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
    
    public ProdutoException(HttpStatus status, String message) {
        super(status, message);
    }
    
    public static ProdutoException produtoNaoEncontrado(Long id) {
        return new ProdutoException(
            HttpStatus.NOT_FOUND,
            "Produto não encontrado com ID: " + id
        );
    }
    
    public static ProdutoException codigoJaExiste(String codigo) {
        return new ProdutoException(
            HttpStatus.CONFLICT,
            "Já existe um produto com o código: " + codigo
        );
    }
    
    public static ProdutoException composicaoCircular(String produtoA, String produtoB) {
        return new ProdutoException(
            "Não é permitida composição circular: " + produtoA + " usa " + produtoB
        );
    }
    
    public static ProdutoException produtoNaoFabricavel(Long id) {
        return new ProdutoException(
            "Apenas produtos do tipo FABRICAVEL podem ter composição. ID: " + id
        );
    }
    
    public static ProdutoException quantidadeInvalida() {
        return new ProdutoException(
            "Quantidade da composição deve ser maior que zero"
        );
    }
    
    public static ProdutoException produtoNaoPodeSerUtilizado(Long id) {
        return new ProdutoException(
            "Produto com ID " + id + " não está ativo e não pode ser utilizado"
        );
    }
}
