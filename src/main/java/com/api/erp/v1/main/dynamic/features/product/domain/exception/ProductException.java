package com.api.erp.v1.main.dynamic.features.product.domain.exception;

import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is a violation of business rule related to products
 */
public class ProductException extends BusinessException {
    
    public ProductException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
    
    public ProductException(HttpStatus status, String message) {
        super(status, message);
    }
    
    public static ProductException productNaoEncontrado(Long id) {
        return new ProductException(
            HttpStatus.NOT_FOUND,
            "Product not found with ID: " + id
        );
    }
    
    public static ProductException codigoJaExiste(String codigo) {
        return new ProductException(
            HttpStatus.CONFLICT,
            "A product with code already exists: " + codigo
        );
    }
    
    public static ProductException compositionCircular(String productA, String productB) {
        return new ProductException(
            "Circular composition is not allowed: " + productA + " uses " + productB
        );
    }
    
    public static ProductException productNaoFabricavel(Long id) {
        return new ProductException(
            "Only products of type MANUFACTURABLE can have composition. ID: " + id
        );
    }
    
    public static ProductException quantidadeInvalida() {
        return new ProductException(
            "Composition quantity must be greater than zero"
        );
    }
    
    public static ProductException productNaoPodeSerUtilizado(Long id) {
        return new ProductException(
            "Product with ID " + id + " is not active and cannot be used"
        );
    }
}
