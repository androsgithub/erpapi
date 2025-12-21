package com.api.erp.features.empresa.domain.validator;

import com.api.erp.features.empresa.domain.entity.Empresa;
import com.api.erp.shared.domain.exception.BusinessException;
import com.api.erp.shared.domain.valueobject.CNPJ;
import com.api.erp.shared.domain.valueobject.Email;
import com.api.erp.shared.domain.valueobject.Telefone;
import com.api.erp.shared.domain.valueobject.CEP;

public class EmpresaValidator {
    
    public static void validarEmpresa(Empresa empresa) {
        validarNome(empresa.getNome());
        validarCnpj(empresa.getCnpj());
        validarEmail(empresa.getEmail());
    }
    
    public static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BusinessException("Nome da empresa é obrigatório");
        }
        if (nome.length() < 3) {
            throw new BusinessException("Nome da empresa deve ter no mínimo 3 caracteres");
        }
        if (nome.length() > 255) {
            throw new BusinessException("Nome da empresa não pode ter mais de 255 caracteres");
        }
    }
    
    public static void validarCnpj(CNPJ cnpj) {
        if (cnpj == null) {
            throw new BusinessException("CNPJ da empresa é obrigatório");
        }
    }
    
    public static void validarEmail(Email email) {
        if (email == null) {
            throw new BusinessException("Email da empresa é obrigatório");
        }
    }
    
    public static void validarTelefone(String telefone) {
        if (telefone != null && !telefone.isBlank()) {
            try {
                new Telefone(telefone);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Telefone inválido");
            }
        }
    }
    
    public static void validarCep(String cep) {
        if (cep != null && !cep.isBlank()) {
            try {
                new CEP(cep);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("CEP inválido");
            }
        }
    }
}
