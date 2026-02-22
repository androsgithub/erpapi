package com.api.erp.v1.main.datasource.routing.domain;

import java.util.Optional;

/**
 * DOMAIN - Interface para Configuração de Tenant
 * 
 * Define contrato para recuperar configurações de DataSource de tenants.
 * Abstração que permite diferentes fontes (BD, Cache, etc) sem exposição de detalhes.
 * 
 * Responsabilidade: Fornecer dados de configuração de tenant
 * 
 * @author ERP System
 * @version 1.0
 */
public interface ITenantConfigProvider {

    /**
     * Retorna a configuração de DataSource para um tenant
     * 
     * @param tenantId Identificador do tenant
     * @return Optional contendo a configuração, ou vazio se não encontrado
     * @throws IllegalArgumentException se tenantId for null ou vazio
     */
    Optional<TenantDSConfig> getTenantConfig(Long tenantId);

    /**
     * Verifica se um tenant existe
     * 
     * @param tenantId Identificador do tenant
     * @return true se existe, false caso contrário
     */
    boolean tenantExists(String tenantId);
}
