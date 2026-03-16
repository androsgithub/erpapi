package com.api.erp.v1.main.migration.service;

import com.api.erp.v1.main.migration.domain.TenantCreatedEvent;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventSource;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - TenantCreationMigrationListener
 *
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantCreationMigrationListener - Testes Unitários")
class TenantCreationMigrationListenerTest {

    @Mock
    private TenantMigrationQueue migrationQueue;

    @Mock
    private TenantDatasourceRepository tenantDatasourceRepository;

    private TenantCreationMigrationListener listener;

    @BeforeEach
    void setUp() {
        listener = new TenantCreationMigrationListener(migrationQueue, tenantDatasourceRepository);
    }

    @Test
    @DisplayName("dado_tenantComDatasource_quando_onTenantCreated_entao_enfileira")
    void testGivenTenantWithDatasource_WhenOnTenantCreated_ThenEnqueues() {
        Tenant tenant = Tenant.builder().id(1L).name("Tenant A").build();
        TenantDatasource datasource = TenantDatasource.builder().id(1L).build();
        TenantCreatedEvent event = new TenantCreatedEvent(this, tenant);

        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(1L)).thenReturn(Optional.ofNullable(datasource));
        when(migrationQueue.enqueueEvent(eq(1L), eq("Tenant A"), eq(datasource),
                eq(MigrationEventSource.TENANT_CREATION)))
                .thenReturn(TenantMigrationEvent.create(1L, "Tenant A", datasource, MigrationEventSource.TENANT_CREATION));

        listener.onTenantCreated(event);

        verify(migrationQueue, times(1)).enqueueEvent(
                eq(1L), eq("Tenant A"), eq(datasource), eq(MigrationEventSource.TENANT_CREATION)
        );
    }

    @Test
    @DisplayName("dado_tenantSemDatasource_quando_onTenantCreated_entao_naoEnfileira")
    void testGivenTenantWithoutDatasource_WhenOnTenantCreated_ThenDoesNotEnqueue() {
        Tenant tenant = Tenant.builder().id(2L).name("Tenant B").build();
        TenantCreatedEvent event = new TenantCreatedEvent(this, tenant);

        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(2L)).thenReturn(null);

        listener.onTenantCreated(event);

        verify(migrationQueue, never()).enqueueEvent(
                anyLong(), anyString(), any(), any()
        );
    }

    @Test
    @DisplayName("dado_excecaoAoEnfileirar_quando_onTenantCreated_entao_naoRelancaExcecao")
    void testGivenExceptionOnEnqueue_WhenOnTenantCreated_ThenDoesNotRethrow() {
        Tenant tenant = Tenant.builder().id(3L).name("Tenant C").build();
        TenantDatasource datasource = TenantDatasource.builder().id(3L).build();
        TenantCreatedEvent event = new TenantCreatedEvent(this, tenant);

        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(3L)).thenReturn(Optional.ofNullable(datasource));
        when(migrationQueue.enqueueEvent(anyLong(), anyString(), any(), any()))
                .thenThrow(new RuntimeException("Queue error"));

        // Não deve lançar exceção
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> listener.onTenantCreated(event));
    }
}
