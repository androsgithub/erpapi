package com.api.erp.v1.features.unidademedida.infrastructure.factory;

import com.api.erp.v1.features.empresa.domain.entity.UnidadeMedidaConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import com.api.erp.v1.features.unidademedida.domain.service.IUnidadeMedidaService;
import com.api.erp.v1.features.unidademedida.domain.validator.UnidadeMedidaValidator;
import com.api.erp.v1.features.unidademedida.infrastructure.service.UnidadeMedidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UnidadeMedidaServiceFactory {
    private final UnidadeMedidaRepository repository;
    private final UnidadeMedidaValidator validator;
    private final IEmpresaService empresaService;

    public UnidadeMedidaServiceFactory(UnidadeMedidaRepository repository, UnidadeMedidaValidator validator, IEmpresaService empresaService) {
        this.repository = repository;
        this.validator = validator;
        this.empresaService = empresaService;
    }


    public IUnidadeMedidaService create() {
        UnidadeMedidaConfig unidadeMedidaConfig = empresaService.getEmpresaConfig().getUnidadeMedidaConfig();
        IUnidadeMedidaService service = new UnidadeMedidaService(repository, validator);
        return service;
    }
}
