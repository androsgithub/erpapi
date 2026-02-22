package com.api.erp.v1.main.datasource.routing.domain;

import javax.sql.DataSource;

/**
 * DOMAIN - Interface para Roteador de DataSources
 * 
 * Define contrato para gerenciar e rotear conexões de banco de dados
 * para diferentes tenants dinamicamente.
 * 
 * Responsabilidade: Abstrair o mecanismo de roteamento de datasources
 * Não conhece detalhes de implementação (HikariCP, Query, etc)
 * 
 * @author ERP System
 * @version 1.0
 */
public interface IDataSourceRouter {

    /**
     * Registra um novo DataSource para um tenant específico
     * 
     * @param tenantId Identificador único do tenant
     * @param dataSource DataSource configurado e pronto para uso
     * @throws IllegalArgumentException se tenantId for null ou vazio
     * @throws IllegalArgumentException se dataSource for null
     */
    void registerDataSource(Long tenantId, DataSource dataSource);

    /**
     * Obtém o DataSource para um tenant
     * 
     * Se não estiver em cache, recupera da configuração de tenants
     * e registra automaticamente para futuras requisições.
     * 
     * @param tenantId Identificador do tenant
     * @return DataSource configurado para o tenant
     * @throws TenantDataSourceNotFoundException se o tenant não for encontrado
     * @throws IllegalArgumentException se tenantId for null ou vazio
     */
    DataSource getDataSource(Long tenantId);

    /**
     * Verifica se existe um DataSource registrado em cache para um tenant
     * 
     * @param tenantId Identificador do tenant
     * @return true se existe em cache, false caso contrário
     */
    boolean isDataSourceCached(Long tenantId);

    /**
     * Remove um DataSource do cache
     * Útil para invalidação quando configuração de tenant mudança
     * 
     * @param tenantId Identificador do tenant
     */
    void invalidateDataSourceCache(Long tenantId);

    /**
     * Obtém o DataSource do Master (banco centralizado)
     * 
     * @return DataSource do master
     */
    DataSource getMasterDataSource();
}
