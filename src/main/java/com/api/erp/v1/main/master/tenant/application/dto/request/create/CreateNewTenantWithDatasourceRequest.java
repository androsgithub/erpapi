package com.api.erp.v1.main.master.tenant.application.dto.request.create;

import com.api.erp.v1.main.shared.domain.enums.TenantType;
import com.dros.taxengine.domain.ContribuinteICMS;
import com.dros.taxengine.domain.RegimeTributario;

/**
 * DTO para criar novo tenant completo com datasource e enfileirar migrações + seeders
 * <p>
 * Fluxo:
 * 1. Cria novo tenant no master database
 * 2. Configura datasource do tenant
 * 3. Enfileira migração (Flyway)
 * 4. Enfileira MainSeed (permissões, usuário admin, etc) após migrações
 *
 * @author ERP System
 * @version 1.0
 */
public record CreateNewTenantWithDatasourceRequest(String name, String cnpj, String razaoSocial, String email,
                                                   String telefone, TenantType tenantType, String tenantSubdomain,
                                                   ContribuinteICMS contribuinteICMS, RegimeTributario regimeTributario,
                                                   String dbHost, Integer dbPort, String dbName, String dbUsername,
                                                   String dbPassword, String dbType) {
}
