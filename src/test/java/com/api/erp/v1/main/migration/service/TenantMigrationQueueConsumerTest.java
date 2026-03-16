package com.api.erp.v1.main.migration.service;

import com.api.erp.v1.main.config.startup.seed.MainSeed;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventSource;
import com.api.erp.v1.main.master.tenant.infrastructure.config.TenantMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - TenantMigrationQueueConsumer
 *
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantMigrationQueueConsumer - Testes Unitários")
class TenantMigrationQueueConsumerTest {

    @Mock
    private TenantMigrationQueue migrationQueue;

    @Mock
    private TenantMigrationService tenantMigrationService;

    @Mock
    private MainSeed mainSeed;

    private TenantMigrationQueueConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new TenantMigrationQueueConsumer(migrationQueue, tenantMigrationService, mainSeed);
    }

    // ===== processEventById =====

    @Test
    @DisplayName("dado_eventIdExistente_quando_processEventById_entao_executaMigracaoESeed")
    void testGivenExistingEventId_WhenProcessEventById_ThenExecutesMigrationAndSeed() throws Exception {
        String eventId = "test-event-1";
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        when(migrationQueue.getEvent(eventId)).thenReturn(Optional.of(event));
        doNothing().when(tenantMigrationService).migrateTenantById(1L);
        doNothing().when(mainSeed).execute();

        consumer.processEventById(eventId);

        verify(tenantMigrationService, times(1)).migrateTenantById(1L);
        verify(mainSeed, times(1)).execute();
        verify(migrationQueue, times(1)).recordEventCompletion(event);
    }

    @Test
    @DisplayName("dado_eventIdInexistente_quando_processEventById_entao_lancaIllegalArgument")
    void testGivenNonExistingEventId_WhenProcessEventById_ThenThrowsIllegalArgument() {
        String eventId = "invalid-event";
        when(migrationQueue.getEvent(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consumer.processEventById(eventId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event not found");
    }

    @Test
    @DisplayName("dado_migracaoFalha_quando_processEventById_entao_lancaException")
    void testGivenMigrationFails_WhenProcessEventById_ThenThrowsException() throws Exception {
        String eventId = "test-event-2";
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        when(migrationQueue.getEvent(eventId)).thenReturn(Optional.of(event));
        doThrow(new RuntimeException("Flyway error")).when(tenantMigrationService).migrateTenantById(1L);

        assertThatThrownBy(() -> consumer.processEventById(eventId))
                .isInstanceOf(Exception.class);
    }
}
