package com.api.erp.v1.main.features.usuario.application.validator;

import com.api.erp.v1.main.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.main.features.usuario.domain.validator.IUsuarioValidator;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class BasicUsuarioValidator implements IUsuarioValidator {
    
    @Override
    public void validar(CreateUsuarioRequest request) {
        validarNome(request.nomeCompleto());
        validarSenha(request.senha());
    }
    
    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BusinessException("Nome é obrigatório");
        }
        if (nome.length() < 3) {
            throw new BusinessException("Nome deve ter pelo menos 3 caracteres");
        }
    }
    
    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new BusinessException("Senha deve ter pelo menos 8 caracteres");
        }
        
        boolean temMaiuscula = senha.chars().anyMatch(Character::isUpperCase);
        boolean temMinuscula = senha.chars().anyMatch(Character::isLowerCase);
        boolean temNumero = senha.chars().anyMatch(Character::isDigit);
        boolean temEspecial = senha.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        if (!temMaiuscula || !temMinuscula || !temNumero || !temEspecial) {
            throw new BusinessException("Senha deve conter maiúsculas, minúsculas, números e caracteres especiais");
        }
    }
}
