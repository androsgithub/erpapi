package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class MeasureUnitConfig {

    @Column(name = "measure_unit_validation_enabled", nullable = false)
    private Boolean measureUnitValidationEnabled = false;

    @Column(name = "measure_unit_cache_enabled", nullable = false)
    private Boolean measureUnitCacheEnabled = false;

    public boolean isMeasureUnitValidationEnabled() {
        return Boolean.TRUE.equals(measureUnitValidationEnabled);
    }

    public boolean isMeasureUnitCacheEnabled() {
        return Boolean.TRUE.equals(measureUnitCacheEnabled);
    }
}
