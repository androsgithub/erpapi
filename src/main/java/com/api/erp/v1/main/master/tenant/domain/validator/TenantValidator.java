package com.api.erp.v1.main.master.tenant.domain.validator;

import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.valueobject.CEP;
import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import com.api.erp.v1.main.shared.domain.valueobject.Telefone;

public class TenantValidator {

    public static void validarTenant(Tenant empresa) {
        validarNome(empresa.getName());
        validarCnpj(empresa.getFiscal().getCnpj());
        validarEmail(empresa.getEmail());
    }

    public static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BusinessException("Company name is required");
        }
        if (nome.length() < 3) {
            throw new BusinessException("Company name must have at least 3 characters");
        }
        if (nome.length() > 255) {
            throw new BusinessException("Company name cannot exceed 255 characters");
        }
    }

    public static void validarCnpj(CNPJ cnpj) {
        if (cnpj == null) {
            throw new BusinessException("Company CNPJ is required");
        }
    }

    public static void validarEmail(Email email) {
        if (email == null) {
            throw new BusinessException("Company email is required");
        }
    }

    public static void validarTelefone(String telefone) {
        if (telefone != null && !telefone.isBlank()) {
            try {
                new Telefone(telefone);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid phone number");
            }
        }
    }

    public static void validarCep(String cep) {
        if (cep != null && !cep.isBlank()) {
            try {
                new CEP(cep);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid ZIP code");
            }
        }
    }
}
