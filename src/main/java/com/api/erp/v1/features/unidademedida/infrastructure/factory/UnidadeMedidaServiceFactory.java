package com.api.erp.v1.features.unidademedida.infrastructure.factory;

import com.api.erp.v1.tenant.domain.service.ITenantService;
import com.api.erp.v1.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import com.api.erp.v1.features.unidademedida.domain.service.IUnidadeMedidaService;
import com.api.erp.v1.features.unidademedida.domain.validator.UnidadeMedidaValidator;
import com.api.erp.v1.features.unidademedida.infrastructure.service.UnidadeMedidaService;
import org.springframework.stereotype.Component;

@Component
public class UnidadeMedidaServiceFactory {
    private final UnidadeMedidaRepository repository;
    private final UnidadeMedidaValidator validator;
    private final ITenantService tenantService;

    public UnidadeMedidaServiceFactory(UnidadeMedidaRepository repository, UnidadeMedidaValidator validator, ITenantService tenantService) {
        this.repository = repository;
        this.validator = validator;
        this.tenantService = tenantService;
    }


    public IUnidadeMedidaService create() {
        IUnidadeMedidaService service = new UnidadeMedidaService(repository, validator);
        return service;
    }
}
