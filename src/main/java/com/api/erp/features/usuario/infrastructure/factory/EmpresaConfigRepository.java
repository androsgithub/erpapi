package com.api.erp.features.usuario.infrastructure.factory;

import com.api.erp.features.empresa.domain.factory.EmpresaConfig;

public interface EmpresaConfigRepository {
    EmpresaConfig findByEmpresaId(String empresaId);
}
