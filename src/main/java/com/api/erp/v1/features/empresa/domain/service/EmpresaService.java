package com.api.erp.v1.features.empresa.domain.service;

import com.api.erp.v1.features.empresa.application.dto.EmpresaRequest;
import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.empresa.domain.factory.EmpresaConfig;

public interface EmpresaService {
    Empresa getDadosEmpresa();
    Empresa updateDadosEmpresa(EmpresaRequest empresaRequest);

    EmpresaConfig getEmpresaConfig();
}
