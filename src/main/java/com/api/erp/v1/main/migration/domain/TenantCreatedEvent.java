package com.api.erp.v1.main.migration.domain;

import com.api.erp.v1.main.tenant.domain.entity.Tenant;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * EVENT - Evento de Creation de Novo Tenant
 * 
 * Publicado quando um novo tenant é criado na aplicação.
 * Dispara automaticamente a enfileiramento do tenant na fila de migrações.
 * 
 * Este evento une os fluxos:
 * - Creation de tenant (TenantService.criarTenant)
 * - Migração do tenant (TenantMigrationQueue)
 * 
 * @author ERP System
 * @version 2.0
 */
@Getter
public class TenantCreatedEvent extends ApplicationEvent {
    
    private final Tenant tenant;
    
    public TenantCreatedEvent(Object source, Tenant tenant) {
        super(source);
        this.tenant = tenant;
    }
}
