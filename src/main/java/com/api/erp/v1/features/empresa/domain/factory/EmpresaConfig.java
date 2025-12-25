package com.api.erp.v1.features.empresa.domain.factory;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
public class EmpresaConfig {

    private boolean requerAprovacaoGestor;
    private boolean requerEmailCorporativo;

    @ElementCollection
    @CollectionTable(
            name = "empresa_dominios",
            joinColumns = @JoinColumn(name = "empresa_id")
    )
    @Column(name = "dominio")
    private List<String> dominiosPermitidos;

    public EmpresaConfig() {
    }

    public EmpresaConfig(boolean requerAprovacaoGestor, boolean requerEmailCorporativo) {
        this.requerAprovacaoGestor = requerAprovacaoGestor;
        this.requerEmailCorporativo = requerEmailCorporativo;
    }
}

