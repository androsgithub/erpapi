package com.api.erp.features.empresa.domain.factory;

import com.api.erp.features.empresa.domain.entity.Empresa;
import com.api.erp.features.empresa.domain.validator.EmpresaValidator;
import java.util.UUID;

public class EmpresaFactory {
    
    public static Empresa criar(String nome, String cnpj, String email) {
        EmpresaValidator.validarEmpresa(new Empresa(nome, cnpj, email));
        
        Empresa empresa = new Empresa(nome, cnpj, email);
        empresa.setId(UUID.randomUUID().toString());
        
        return empresa;
    }
    
    public static Empresa criar(String nome, String cnpj, String email, EmpresaConfig config) {
        Empresa empresa = criar(nome, cnpj, email);
        
        if (config != null) {
            empresa.setRequerAprovacaoGestor(config.isRequerAprovacaoGestor());
            empresa.setRequerEmailCorporativo(config.isRequerEmailCorporativo());
            empresa.setDominiosPermitidos(config.getDominiosPermitidos());
        }
        
        return empresa;
    }
}
