package com.api.erp.v1.main.migration.jobs;

import com.api.erp.v1.main.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * READER - Leitura de Datasources de Tenants
 * 
 * Responsável por:
 * 1. Carregar todos os tenants ativos
 * 2. Buscar seus datasources correspondentes
 * 3. Fornecer um item por vez para o processor
 * 
 * Características:
 * - É inicializado uma vez no início do step (via @BeforeStep)
 * - Retorna um datasource por leitura
 * - Retorna null quando não há mais itens
 * - Thread-safe
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantMigrationReader implements ItemReader<TenantDatasource> {

    private final TenantRepository tenantRepository;
    private final TenantDatasourceRepository datasourceRepository;
    private Iterator<TenantDatasource> iterator;

    /**
     * Executado uma única vez antes do step iniciar
     * 
     * Carrega todos os tenants ativos e seus datasources
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("");
        log.info("🔄 Inicializando Reader - Carregando Tenants...");

        try {
            // Busca todos os tenants ativos
            var activeTenants = tenantRepository.findAllByAtivaTrue();

            log.info("   📍 Encontrados {} tenant(s) ativo(s)", activeTenants.size());

            // Carrega datasources para cada tenant ativo
            List<TenantDatasource> datasources = activeTenants.stream()
                    .peek(tenant -> log.debug("   • {}", tenant.getNome()))
                    .map(tenant -> datasourceRepository.findByTenantIdAndStatus(tenant.getId(), true))
                    .filter(Objects::nonNull)
                    .toList();

            if (datasources.isEmpty()) {
                log.warn("⚠️ Nenhum datasource ativo encontrado");
            } else {
                log.info("   ✅ {} datasource(s) carregado(s) com sucesso", datasources.size());
            }

            // Cria iterator para iterar através dos datasources
            this.iterator = datasources.iterator();

        } catch (Exception e) {
            log.error("❌ Erro ao inicializar Reader: {}", e.getMessage(), e);
            this.iterator = java.util.Collections.emptyIterator();
        }
    }

    /**
     * Chamado repetidamente para obter o próximo item
     * 
     * @return próximo TenantDatasource ou null se fim da lista
     */
    @Override
    public TenantDatasource read() {
        if (iterator == null || !iterator.hasNext()) {
            return null;
        }

        TenantDatasource datasource = iterator.next();
        log.debug("   📤 Lido datasource do tenant: {}", datasource.getTenant().getId());

        return datasource;
    }
}
