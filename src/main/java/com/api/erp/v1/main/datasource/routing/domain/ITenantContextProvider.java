package com.api.erp.v1.main.datasource.routing.domain;

/**
 * DOMAIN - Interface para Provider de Contexto de Tenant
 * 
 * Define contrato para gerenciar o contexto do tenant na request atual.
 * Agnóstica a implementação (ThreadLocal, RequestContext, etc).
 * 
 * Responsabilidade: Abstrair o mecanismo de armazenamento do contexto do tenant
 * 
 * @author ERP System
 * @version 1.0
 */
public interface ITenantContextProvider {

    /**
     * Define o tenant atual para o contexto de request
     * 
     * @param tenantId ID do tenant a ser definido
     * @throws IllegalArgumentException se tenantId for null ou vazio
     */
    void setCurrentTenant(String tenantId);

    /**
     * Recupera o tenant atual do contexto
     * 
     * @return ID do tenant atual, ou null se não houver contexto definido
     */
    String getCurrentTenant();

    /**
     * Limpa o contexto do tenant
     * Importante chamar isso ao final da request para evitar vazamentos de memória
     */
    void clearContext();

    /**
     * Verifica se há um tenant definido no contexto atual
     * 
     * @return true se há um tenant, false caso contrário
     */
    default boolean hasTenant() {
        return getCurrentTenant() != null;
    }
}
