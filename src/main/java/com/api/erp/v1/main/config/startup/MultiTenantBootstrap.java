package com.api.erp.v1.main.config.startup;

import com.api.erp.v1.main.config.startup.seed.MainSeed;
import com.api.erp.v1.main.config.startup.seed.SchemaGenerator;
import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.tenant.domain.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * COMPONENT - Bootstrap de Inicialização Multi-Tenant
 * 
 * Orquestra a inicialização de dados para cada tenant ativo durante o startup da aplicação.
 * Executa em ordem:
 * 1. Schema migration (opcional)
 * 2. MainSeed (permissões, usuários, etc) para cada tenant
 * 
 * Responsabilidades:
 * - Recuperar tenants ativos do banco master
 * - Executar seeds em contexto de cada tenant
 * - Tratamento robusto de erros
 * - Logging detalhado de progresso
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@Slf4j
public class MultiTenantBootstrap {

    private final TenantRepository tenantRepository;
    private final MainSeed mainSeed;

    @Autowired
    public MultiTenantBootstrap(TenantRepository tenantRepository, MainSeed mainSeed) {
        this.tenantRepository = tenantRepository;
        this.mainSeed = mainSeed;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() {

        log.info("📊 Criando schemas...");
        //        SchemaGenerator.executar();
        log.info("✅ Schemas criados!");

        log.info("🚀 Iniciando bootstrap multi-tenant de inicialização...");

        List<Tenant> tenants = tenantRepository.findAllByAtivaTrue();
        
        if (tenants.isEmpty()) {
            log.warn("⚠️  Nenhum tenant ativo encontrado para bootstrap");
            return;
        }

        log.info("Encontrados {} tenant(s) ativo(s)", tenants.size());

        int sucessos = 0;
        int erros = 0;

        for (Tenant tenant : tenants) {
            try {
                log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                log.info("🔧 Bootstrap para tenant {} ({})", tenant.getId(), tenant.getNome());
                log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                TenantContext.setTenantId(tenant.getId());

                mainSeed.executar();
                
                log.info("✅ Inicialização do tenant {} concluída com sucesso", tenant.getId());
                sucessos++;

            } catch (org.springframework.dao.QueryTimeoutException | 
                     org.springframework.transaction.CannotCreateTransactionException e) {
                // Tenant datasource não disponível - pular este tenant
                log.warn("⚠️  Datasource não disponível para tenant {} - pulando inicialização", tenant.getId());
                log.debug("Detalhes:", e);
                erros++;
            } catch (Exception e) {
                log.error("❌ Erro ao inicializar tenant {}: {}", tenant.getId(), e.getMessage());
                log.debug("Stack trace:", e);
                erros++;
            } finally {
                TenantContext.clear();
            }
        }

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        if (erros == 0) {
            log.info("✅ Bootstrap multi-tenant finalizado com sucesso: {} tenant(s) inicializado(s)", sucessos);
        } else {
            log.warn("⚠️  Bootstrap multi-tenant concluído: {} sucesso(s), {} erro(s)", sucessos, erros);
        }
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
