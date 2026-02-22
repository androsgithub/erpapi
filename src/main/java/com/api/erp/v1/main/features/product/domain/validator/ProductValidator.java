package com.api.erp.v1.main.features.product.domain.validator;

import com.api.erp.v1.main.features.product.domain.entity.Product;
import com.api.erp.v1.main.features.product.domain.entity.ProductComposition;
import com.api.erp.v1.main.features.product.domain.exception.ProductException;
import com.api.erp.v1.main.features.product.domain.repository.ProductCompositionRepository;
import com.api.erp.v1.main.shared.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Validador de Product
 * 
 * SRP: Responsável única por validações de domínio
 */
@Component
@RequiredArgsConstructor
public class ProductValidator {
    
    private final ProductCompositionRepository compositionRepository;
    
    /**
     * Validates basic product data
     * 
     * Note: Fiscal classification validation (NCM, origin, etc) is performed
     * in Value Objects (NCM, OrigemMercadoria, etc) in the application service
     */
    public void validarCriacao(String codigo, String descricao) {
        validarCodigo(codigo);
        validarDescricao(descricao);
    }
    
    /**
     * Validates product code
     */
    public void validarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ValidationException("codigo", "Product code is required");
        }
        
        if (codigo.length() > 50) {
            throw new ValidationException("codigo", "Code cannot exceed 50 characters");
        }
        
        if (!codigo.matches("^[A-Z0-9-._]+$")) {
            throw new ValidationException(
                "codigo",
                "Code must contain only uppercase letters, numbers, hyphen, period and underscore"
            );
        }
    }
    
    /**
     * Validates description
     */
    public void validarDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new ValidationException("descricao", "Product description is required");
        }
        
        if (descricao.length() > 255) {
            throw new ValidationException(
                "descricao",
                "Description cannot exceed 255 characters"
            );
        }
    }
    
    /**
     * Validates NCM
     */
    public void validarNCM(String ncm) {
        if (ncm == null || ncm.trim().isEmpty()) {
            throw new ValidationException("ncm", "NCM is required");
        }
        
        String ncmLimpo = ncm.replaceAll("[^0-9]", "");
        
        if (ncmLimpo.length() != 8) {
            throw new ValidationException(
                "ncm",
                "NCM must contain exactly 8 digits"
            );
        }
        
        if (!ncmLimpo.matches("^[0-9]{8}$")) {
            throw new ValidationException(
                "ncm",
                "NCM must contain only numbers"
            );
        }
    }
    
    /**
     * Validates a product composition
     */
    public void validarComposition(Product productFabricado, Product componente, BigDecimal quantidade) {
        // Validate if product is manufacturable
        if (!productFabricado.ehFabricavel()) {
            throw ProductException.productNaoFabricavel(productFabricado.getId());
        }
        
        // Validate quantity
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw ProductException.quantidadeInvalida();
        }
        
        // Validate if there is no circular composition
        validarCompositionCircular(productFabricado, componente);
        
        // Validate if component is active
        if (!componente.podeSerUtilizado()) {
            throw ProductException.productNaoPodeSerUtilizado(componente.getId());
        }
    }
    
    /**
     * Validates if there is no circular composition
     * Example: If Product A uses Product B, then Product B cannot use Product A
     */
    private void validarCompositionCircular(Product productFabricado, Product componente) {
        // If the component equals the manufactured product, it is circular
        if (productFabricado.getId().equals(componente.getId())) {
            throw ProductException.compositionCircular(
                productFabricado.getCodigo(),
                componente.getCodigo()
            );
        }
        
        // Check if the component uses the manufactured product in its composition
        // This prevents simple circular compositions
        if (ehComponenteDe(componente, productFabricado)) {
            throw ProductException.compositionCircular(
                productFabricado.getCodigo(),
                componente.getCodigo()
            );
        }
    }
    
    /**
     * Checks if a product is a component of another (simple search)
     */
    private boolean ehComponenteDe(Product possibleComponente, Product possibleFabricado) {
        var composicoes = compositionRepository.findByProductFabricadoId(possibleComponente.getId());
        
        for (ProductComposition comp : composicoes) {
            if (comp.getProductComponente().getId().equals(possibleFabricado.getId())) {
                return true;
            }
        }
        
        return false;
    }
}
