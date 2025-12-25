package com.api.erp.v1.features.unidademedida.domain.validator;

import com.api.erp.v1.shared.domain.exception.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Validador de UnidadeMedida
 * 
 * SRP: Responsável única por validações de domínio
 */
@Component
public class UnidadeMedidaValidator {
    
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
            throw new ValidationException("sigla", "A sigla é obrigatória");
        }
        
        if (sigla.length() > 10) {
            throw new ValidationException("sigla", "A sigla não pode ter mais de 10 caracteres");
        }
        
        if (!sigla.matches("^[A-Z0-9]+$")) {
            throw new ValidationException("sigla", "A sigla deve conter apenas letras maiúsculas e números");
        }
    }
    
    /**
     * Valida a descrição
     */
    public void validarDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new ValidationException("descricao", "A descrição é obrigatória");
        }
        
        if (descricao.length() > 100) {
            throw new ValidationException("descricao", "A descrição não pode ter mais de 100 caracteres");
        }
    }
}
