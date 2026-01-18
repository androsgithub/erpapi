package com.api.erp.v1.tenant.infrastructure.config.datasource.manual;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * TenantsDatabaseInitializer (DESATIVADO)
 * 
 * Esta classe foi DESATIVADA na migração para DATABASE-per-TENANT puro.
 * 
 * Em uma arquitetura DATABASE-per-TENANT:
 * - Cada tenant tem seu próprio banco de dados (não schema)
 * - Os bancos de dados são criados fora da aplicação
 * - As migrações são executadas apenas no master database via FlywayConfig
 * - Não há necessidade de criar múltiplos schemas
 * 
 * HISTÓRICO: Esta classe foi originalmente usada na arquitetura SCHEMA-per-TENANT,
 * onde cada tenant tinha seu próprio schema no mesmo banco de dados.
 */
@Slf4j
@Configuration
public class TenantsDatabaseInitializer {
    // DESATIVADA: Veja os comentários acima para entender por quê
}
