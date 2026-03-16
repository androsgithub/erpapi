package com.api.erp.v1.main.migration.startup;

import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventSource;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import com.api.erp.v1.main.migration.service.TenantMigrationQueueConsumer;
import com.api.erp.v1.main.master.tenant.domain.entity.Tenant;
import com.api.erp.v1.main.master.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - TenantMigrationStartupWorker
 *
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantMigrationStartupWorker - Testes Unitários")
class TenantMigrationStartupWorkerTest {

    @Mock
    private TenantMigrationQueue migrationQueue;

    @Mock
    private TenantMigrationQueueConsumer queueConsumer;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantDatasourceRepository tenantDatasourceRepository;

    private TenantMigrationStartupWorker worker;

    @BeforeEach
    void setUp() {
        worker = new TenantMigrationStartupWorker(
                migrationQueue, queueConsumer, tenantRepository, tenantDatasourceRepository
        );
    }

    @Test
    @DisplayName("dado_tenantsAtivos_quando_initializeAndStart_entao_enfileiraTodosEIniciaConsumer")
    void testGivenActiveTenants_WhenInitializeAndStart_ThenEnqueuesAllAndStartsConsumer() {
        Tenant t1 = Tenant.builder().id(1L).name("Tenant A").active(true).build();
        Tenant t2 = Tenant.builder().id(2L).name("Tenant B").active(true).build();
        TenantDatasource ds1 = TenantDatasource.builder().id(1L).build();
        TenantDatasource ds2 = TenantDatasource.builder().id(2L).build();

        when(tenantRepository.findAllByActiveTrue()).thenReturn(Arrays.asList(t1, t2));
        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(1L)).thenReturn(Optional.ofNullable(ds1));
        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(2L)).thenReturn(Optional.ofNullable(ds2));
        when(migrationQueue.enqueueEvent(anyLong(), anyString(), any(), any()))
                .thenReturn(TenantMigrationEvent.create(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP));

        worker.initializeAndStart();

        verify(migrationQueue, times(2)).enqueueEvent(anyLong(), anyString(), any(),
                eq(MigrationEventSource.APPLICATION_STARTUP));
        verify(queueConsumer, times(1)).startConsumer();
    }

    @Test
    @DisplayName("dado_semTenantsAtivos_quando_initializeAndStart_entao_retornaSemEnfileirar")
    void testGivenNoActiveTenants_WhenInitializeAndStart_ThenReturnsWithoutEnqueuing() {
        when(tenantRepository.findAllByActiveTrue()).thenReturn(Collections.emptyList());

        worker.initializeAndStart();

        verify(migrationQueue, never()).enqueueEvent(anyLong(), anyString(), any(), any());
        verify(queueConsumer, never()).startConsumer();
    }

    @Test
    @DisplayName("dado_tenantSemDatasource_quando_initializeAndStart_entao_skipIndividualEContinua")
    void testGivenTenantWithoutDatasource_WhenInitializeAndStart_ThenSkipsAndContinues() {
        Tenant t1 = Tenant.builder().id(1L).name("Tenant A").active(true).build();
        Tenant t2 = Tenant.builder().id(2L).name("Tenant B").active(true).build();
        TenantDatasource ds2 = TenantDatasource.builder().id(2L).build();

        when(tenantRepository.findAllByActiveTrue()).thenReturn(Arrays.asList(t1, t2));
        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(1L)).thenReturn(null); // sem datasource
        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(2L)).thenReturn(Optional.ofNullable(ds2));
        when(migrationQueue.enqueueEvent(anyLong(), anyString(), any(), any()))
                .thenReturn(TenantMigrationEvent.create(2L, "B", null, MigrationEventSource.APPLICATION_STARTUP));

        worker.initializeAndStart();

        // Apenas tenant2 deve ser enfileirado
        verify(migrationQueue, times(1)).enqueueEvent(anyLong(), anyString(), any(), any());
        verify(queueConsumer, times(1)).startConsumer();
    }

    @Test
    @DisplayName("dado_excecaoGeral_quando_initializeAndStart_entao_naoRelanca")
    void testGivenGeneralException_WhenInitializeAndStart_ThenDoesNotRethrow() {
        when(tenantRepository.findAllByActiveTrue())
                .thenThrow(new RuntimeException("Database unavailable"));

        assertDoesNotThrow(() -> worker.initializeAndStart());
    }

    @Test
    @DisplayName("dado_excecaoAoEnfileirarTenant_quando_initializeAndStart_entao_continuaComProximo")
    void testGivenExceptionOnEnqueue_WhenInitializeAndStart_ThenContinuesWithNext() {
        Tenant t1 = Tenant.builder().id(1L).name("Tenant A").active(true).build();
        Tenant t2 = Tenant.builder().id(2L).name("Tenant B").active(true).build();
        TenantDatasource ds1 = TenantDatasource.builder().id(1L).build();
        TenantDatasource ds2 = TenantDatasource.builder().id(2L).build();

        when(tenantRepository.findAllByActiveTrue()).thenReturn(Arrays.asList(t1, t2));
        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(1L)).thenReturn(Optional.ofNullable(ds1));
        when(tenantDatasourceRepository.findByTenantIdAndActiveTrue(2L)).thenReturn(Optional.ofNullable(ds2));
        when(migrationQueue.enqueueEvent(eq(1L), anyString(), any(), any()))
                .thenThrow(new RuntimeException("Queue error for tenant 1"));
        when(migrationQueue.enqueueEvent(eq(2L), anyString(), any(), any()))
                .thenReturn(TenantMigrationEvent.create(2L, "B", null, MigrationEventSource.APPLICATION_STARTUP));

        assertDoesNotThrow(() -> worker.initializeAndStart());

        // Ambos devem ser tentados
        verify(migrationQueue, times(1)).enqueueEvent(eq(1L), anyString(), any(), any());
        verify(migrationQueue, times(1)).enqueueEvent(eq(2L), anyString(), any(), any());
    }
}
