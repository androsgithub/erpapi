package com.api.erp.v1.main.features.measureunit.infrastructure.factory;

import com.api.erp.v1.main.tenant.domain.service.ITenantService;
import com.api.erp.v1.main.features.measureunit.domain.repository.MeasureUnitRepository;
import com.api.erp.v1.main.features.measureunit.domain.service.IMeasureUnitService;
import com.api.erp.v1.main.features.measureunit.domain.validator.MeasureUnitValidator;
import com.api.erp.v1.main.features.measureunit.infrastructure.service.MeasureUnitService;
import org.springframework.stereotype.Component;

@Component
public class MeasureUnitServiceFactory {
    private final MeasureUnitRepository repository;
    private final MeasureUnitValidator validator;
    private final ITenantService tenantService;

    public MeasureUnitServiceFactory(MeasureUnitRepository repository, MeasureUnitValidator validator, ITenantService tenantService) {
        this.repository = repository;
        this.validator = validator;
        this.tenantService = tenantService;
    }


    public IMeasureUnitService create() {
        IMeasureUnitService service = new MeasureUnitService(repository, validator);
        return service;
    }
}
