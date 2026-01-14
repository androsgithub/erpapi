package com.api.erp.v1.features.tenant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class UnidadeMedidaConfig {

    @Column(name = "unidade_medida_validation_enabled", nullable = false)
    private Boolean unidadeMedidaValidationEnabled = false;

    @Column(name = "unidade_medida_cache_enabled", nullable = false)
    private Boolean unidadeMedidaCacheEnabled = false;

    public boolean isUnidadeMedidaValidationEnabled() {
        return Boolean.TRUE.equals(unidadeMedidaValidationEnabled);
    }

    public boolean isUnidadeMedidaCacheEnabled() {
        return Boolean.TRUE.equals(unidadeMedidaCacheEnabled);
    }
}
