package com.api.erp.v1.features.usuario.infrastructure.factory;

import com.api.erp.v1.features.empresa.domain.factory.EmpresaConfig;

public interface EmpresaConfigRepository {
    EmpresaConfig findByEmpresaId(String empresaId);
}
