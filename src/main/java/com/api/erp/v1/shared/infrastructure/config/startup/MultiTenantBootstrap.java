package com.api.erp.v1.shared.infrastructure.config.startup;

import com.api.erp.v1.shared.infrastructure.config.startup.seed.*;
import com.api.erp.v1.tenant.domain.entity.Tenant;
import com.api.erp.v1.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

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

        log.info("Criando schemas...");
        SchemaGenerator.executar();
        log.info("Schemas criados!");

        log.info("🚀 Iniciando bootstrap multi-tenant de permissões...");

        List<Tenant> tenants = tenantRepository.findAllByAtivaTrue();

        for (Tenant tenant : tenants) {
            try {
                TenantContext.setTenantId(tenant.getId());

                mainSeed.executar();


            } catch (Exception e) {
                log.error("❌ Erro ao inicializar permissões do tenant {}", tenant.getId(), e);
            } finally {
                TenantContext.clear();
            }
        }

        log.info("✅ Bootstrap multi-tenant finalizado");
    }
}
