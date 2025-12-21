package com.api.erp.features.empresa.domain.factory;

import java.util.List;

public class EmpresaConfig {
    private boolean requerAprovacaoGestor;
    private boolean requerEmailCorporativo;
    private List<String> dominiosPermitidos;
    
    // Construtores
    public EmpresaConfig() {}
    
    public EmpresaConfig(boolean requerAprovacaoGestor, boolean requerEmailCorporativo) {
        this.requerAprovacaoGestor = requerAprovacaoGestor;
        this.requerEmailCorporativo = requerEmailCorporativo;
    }
    
    // Getters e Setters
    public boolean isRequerAprovacaoGestor() {
        return requerAprovacaoGestor;
    }

    public void setRequerAprovacaoGestor(boolean requerAprovacaoGestor) {
        this.requerAprovacaoGestor = requerAprovacaoGestor;
    }

    public boolean isRequerEmailCorporativo() {
        return requerEmailCorporativo;
    }

    public void setRequerEmailCorporativo(boolean requerEmailCorporativo) {
        this.requerEmailCorporativo = requerEmailCorporativo;
    }

    public List<String> getDominiosPermitidos() {
        return dominiosPermitidos;
    }

    public void setDominiosPermitidos(List<String> dominiosPermitidos) {
        this.dominiosPermitidos = dominiosPermitidos;
    }
}
