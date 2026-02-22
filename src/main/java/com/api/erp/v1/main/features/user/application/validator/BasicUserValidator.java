package com.api.erp.v1.main.features.user.application.validator;

import com.api.erp.v1.main.features.user.application.dto.request.CreateUserRequest;
import com.api.erp.v1.main.features.user.domain.validator.IUserValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class BasicUserValidator implements IUserValidator {
    
    @Override
    public void validar(CreateUserRequest request) {
        validarNome(request.nomeCompleto());
        validarSenha(request.senha());
    }
    
    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BusinessException("Name is required");
        }
        if (nome.length() < 3) {
            throw new BusinessException("Name must have at least 3 characters");
        }
    }
    
    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new BusinessException("Password must have at least 8 characters");
        }
        
        boolean temMaiuscula = senha.chars().anyMatch(Character::isUpperCase);
        boolean temMinuscula = senha.chars().anyMatch(Character::isLowerCase);
        boolean temNumero = senha.chars().anyMatch(Character::isDigit);
        boolean temEspecial = senha.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        if (!temMaiuscula || !temMinuscula || !temNumero || !temEspecial) {
            throw new BusinessException("Password must contain uppercase, lowercase, numbers and special characters");
        }
    }
}
