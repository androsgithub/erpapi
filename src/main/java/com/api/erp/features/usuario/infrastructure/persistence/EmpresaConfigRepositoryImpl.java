package com.api.erp.features.usuario.infrastructure.persistence;

import com.api.erp.features.empresa.domain.factory.EmpresaConfig;
import com.api.erp.features.usuario.infrastructure.factory.EmpresaConfigRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class EmpresaConfigRepositoryImpl implements EmpresaConfigRepository {
    
    // Simple in-memory implementation - replace with database access as needed
    private final Map<String, EmpresaConfig> configs = new HashMap<>();
    
    @Override
    public EmpresaConfig findByEmpresaId(String empresaId) {
        return configs.computeIfAbsent(empresaId, k -> new EmpresaConfig());
    }
}
