package com.api.erp.v1.features.empresa.infrastructure.service;

import com.api.erp.v1.features.empresa.domain.entity.ClienteConfig;
import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.empresa.domain.entity.EmpresaConfig;
import com.api.erp.v1.features.empresa.domain.entity.TenantConfig;
import com.api.erp.v1.features.empresa.domain.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final EmpresaRepository empresaRepository;

    /**
     * Busca a config completa da empresa
     */
    @Cacheable(value = "empresaConfig", key = "'config'")
    public EmpresaConfig getEmpresaConfig() {
        return empresaRepository.findById(1L)
                .map(Empresa::getConfig)
                .orElse(new EmpresaConfig());
    }

    /**
     * Busca apenas a ClienteConfig
     */
    @Cacheable(value = "clienteConfig", key = "'config'")
    public ClienteConfig getClienteConfig() {
        return getEmpresaConfig().getClienteConfig();
    }

    @Cacheable(value = "tenantConfig", key = "'config'")
    public TenantConfig getTenantConfig() {
        return getEmpresaConfig().getTenantConfig();
    }

    /**
     * Verifica se validação de cliente está habilitada
     */
    public boolean isClienteValidationEnabled() {
        return getClienteConfig().isClienteValidationEnabled();
    }

    /**
     * Verifica se customizações de tenant estão habilitadas para cliente
     */
    public boolean isClienteTenantCustomizationEnabled() {
        return getClienteConfig().isClienteTenantCustomizationEnabled();
    }
}