package com.api.erp.v1.main.dynamic.features.measureunit.domain.validator;

import com.api.erp.v1.main.shared.domain.exception.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Validador de MeasureUnit
 * 
 * SRP: Responsável única por validações de domínio
 */
@Component
public class MeasureUnitValidator {
    
    /**
     * Valida os dados básicos de uma unidade de medida
     */
    public void validarCriacao(String sigla, String descricao) {
        validarSigla(sigla);
        validarDescricao(descricao);
    }
    
    /**
     * Valida a sigla
     */
    public void validarSigla(String sigla) {
        if (sigla == null || sigla.trim().isEmpty()) {
            throw new ValidationException("sigla", "Code is required");
        }
        
        if (sigla.length() > 10) {
            throw new ValidationException("sigla", "Code cannot exceed 10 characters");
        }
        
        if (!sigla.matches("^[A-Z0-9]+$")) {
            throw new ValidationException("sigla", "Code must contain only uppercase letters and numbers");
        }
    }
    
    /**
     * Valida a descrição
     */
    public void validarDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new ValidationException("descricao", "Description is required");
        }
        
        if (descricao.length() > 100) {
            throw new ValidationException("descricao", "Description cannot have more than 100 characters");
        }
    }
}
