package com.api.erp.v1.main.migration.integration;

import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventSource;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventStatus;
import com.api.erp.v1.main.migration.service.TenantMigrationQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES DE INTEGRAÇÃO - Migration Queue (Full Lifecycle)
 *
 * Fluxo: create → enqueue → poll → process → complete/retry
 * Sem mocks - usa instâncias reais de TenantMigrationEvent + TenantMigrationQueue.
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("Migration Queue - Testes de Integração")
class MigrationQueueIntegrationTest {

    private TenantMigrationQueue queue;

    @BeforeEach
    void setUp() {
        queue = new TenantMigrationQueue();
    }

    @Test
    @DisplayName("dado_3Eventos_quando_fluxoCompletoEnqueuePollProcess_entao_statsCorretas")
    void testGiven3Events_WhenFullEnqueuePollProcess_ThenStatsAreCorrect() {
        // Enfileirar 3 eventos de fontes diferentes
        TenantMigrationEvent e1 = queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        TenantMigrationEvent e2 = queue.enqueueEvent(2L, "B", null, MigrationEventSource.TENANT_CREATION);
        TenantMigrationEvent e3 = queue.enqueueEvent(3L, "C", null, MigrationEventSource.MANUAL_REQUEST);

        assertThat(queue.getQueueSize()).isEqualTo(3);

        // Poll e processar evento 1 — sucesso
        TenantMigrationEvent polled1 = queue.pollNextNonBlocking();
        assertThat(polled1).isNotNull();
        polled1.markStarted();
        assertThat(polled1.getStatus()).isEqualTo(MigrationEventStatus.IN_PROGRESS);
        polled1.markSuccess(1, 2);
        queue.recordEventCompletion(polled1);

        // Poll e processar evento 2 — falha
        TenantMigrationEvent polled2 = queue.pollNextNonBlocking();
        assertThat(polled2).isNotNull();
        polled2.markStarted();
        polled2.markFailed("Database unavailable");
        queue.recordEventCompletion(polled2);

        // Poll e processar evento 3 — sucesso
        TenantMigrationEvent polled3 = queue.pollNextNonBlocking();
        assertThat(polled3).isNotNull();
        polled3.markStarted();
        polled3.markSuccess(3, 1);
        queue.recordEventCompletion(polled3);

        // Verificar fila vazia
        assertThat(queue.isQueueEmpty()).isTrue();

        // Verificar stats
        TenantMigrationQueue.MigrationQueueStats stats = queue.getStats();
        assertThat(stats.getTotalEvents()).isEqualTo(3);
        assertThat(stats.getCompletedEvents()).isEqualTo(2);
        assertThat(stats.getFailedEvents()).isEqualTo(1);
        assertThat(stats.getProgress()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("dado_eventoComRetry_quando_retryFlow_entao_retryCountIncrementado")
    void testGivenEventWithRetry_WhenRetryFlow_ThenRetryCountIncremented() {
        TenantMigrationEvent event = queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);

        // Poll
        TenantMigrationEvent polled = queue.pollNextNonBlocking();
        assertThat(polled).isNotNull();

        // Marcar como falha e retry
        polled.markStarted();
        polled.markFailed("Connection timeout");

        queue.enqueueForRetry(polled);

        assertThat(polled.getRetryCount()).isEqualTo(1);
        assertThat(polled.getStatus()).isEqualTo(MigrationEventStatus.PENDING);
        assertThat(queue.getQueueSize()).isEqualTo(1);

        // Segundo retry
        TenantMigrationEvent polled2 = queue.pollNextNonBlocking();
        polled2.markStarted();
        polled2.markFailed("Still failing");
        queue.enqueueForRetry(polled2);

        assertThat(polled2.getRetryCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("dado_deduplicacao_quando_enfileirarMesmoTenant_entao_retornaExistente")
    void testGivenDeduplication_WhenEnqueueSameTenant_ThenReturnsExisting() {
        TenantMigrationEvent first = queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        TenantMigrationEvent second = queue.enqueueEvent(1L, "A", null, MigrationEventSource.TENANT_CREATION);

        assertThat(first.getEventId()).isEqualTo(second.getEventId());
        assertThat(queue.getQueueSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("dado_multiplosTenants_quando_filtros_entao_retornaCorretamente")
    void testGivenMultipleTenants_WhenFilters_ThenReturnsCorrectly() {
        TenantMigrationEvent e1 = queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        TenantMigrationEvent e2 = queue.enqueueEvent(2L, "B", null, MigrationEventSource.APPLICATION_STARTUP);

        // e1 = IN_PROGRESS
        queue.pollNextNonBlocking();
        e1.markStarted();

        // Verificar filtros
        Collection<TenantMigrationEvent> pending = queue.getPendingEvents();
        Collection<TenantMigrationEvent> inProgress = queue.getInProgressEvents();
        assertThat(pending).hasSize(1);
        assertThat(inProgress).hasSize(1);

        // Completar e1
        e1.markSuccess(1, 0);
        queue.recordEventCompletion(e1);

        Collection<TenantMigrationEvent> completed = queue.getCompletedEvents();
        assertThat(completed).hasSize(1);
    }

    @Test
    @DisplayName("dado_filaComEventos_quando_clearEReinfileirar_entao_filaResetada")
    void testGivenQueueWithEvents_WhenClearAndReEnqueue_ThenQueueReset() {
        queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        queue.enqueueEvent(2L, "B", null, MigrationEventSource.APPLICATION_STARTUP);
        assertThat(queue.getQueueSize()).isEqualTo(2);

        queue.clear();
        assertThat(queue.isQueueEmpty()).isTrue();
        assertThat(queue.getQueueSize()).isEqualTo(0);

        // Pode enfileirar novamente após clear
        queue.enqueueEvent(3L, "C", null, MigrationEventSource.MANUAL_REQUEST);
        assertThat(queue.getQueueSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("dado_maxRetries_quando_enqueueForRetry_entao_marcaFailed")
    void testGivenMaxRetries_WhenEnqueueForRetry_ThenMarksFailed() {
        TenantMigrationEvent event = queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        queue.pollNextNonBlocking();

        // Atingir maxRetries (3)
        event.markForRetry(); // 1
        event.markForRetry(); // 2
        event.markForRetry(); // 3

        event.markStarted();
        event.markFailed("Final error");

        queue.enqueueForRetry(event);

        assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.FAILED);
    }
}
