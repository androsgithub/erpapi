package com.api.erp.v1.main.migration.service;

import com.api.erp.v1.main.migration.domain.TenantMigrationEvent;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventSource;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - TenantMigrationQueue
 *
 * Cobre enqueue, poll, retry, stats, deduplication, filters.
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("TenantMigrationQueue - Testes Unitários")
class TenantMigrationQueueTest {

    private TenantMigrationQueue queue;

    @BeforeEach
    void setUp() {
        queue = new TenantMigrationQueue();
    }

    // ===== enqueueEvent =====

    @Test
    @DisplayName("dado_eventoValido_quando_enqueueEvent_entao_adicionaNaFilaENoRegistry")
    void testGivenValidEvent_WhenEnqueueEvent_ThenAddsToQueueAndRegistry() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        queue.enqueueEvent(event);

        assertThat(queue.getQueueSize()).isEqualTo(1);
        assertThat(queue.isQueueEmpty()).isFalse();
        assertThat(queue.getEvent(event.getEventId())).isPresent();
    }

    @Test
    @DisplayName("dado_tenantSemMigracaoPendente_quando_enqueueEventShortcut_entao_criaEEnfileira")
    void testGivenTenantWithoutPendingMigration_WhenEnqueueShortcut_ThenCreatesAndEnqueues() {
        TenantMigrationEvent result = queue.enqueueEvent(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        assertThat(result).isNotNull();
        assertThat(result.getTenantId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(MigrationEventStatus.PENDING);
        assertThat(queue.getQueueSize()).isEqualTo(1);
    }

    // ===== Deduplication =====

    @Test
    @DisplayName("dado_tenantComMigracaoPendente_quando_enqueueEventShortcut_entao_retornaExistente")
    void testGivenTenantWithPendingMigration_WhenEnqueueShortcut_ThenReturnsExisting() {
        TenantMigrationEvent first = queue.enqueueEvent(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        TenantMigrationEvent second = queue.enqueueEvent(
                1L, "Tenant A", null, MigrationEventSource.TENANT_CREATION
        );

        // Deve retornar o mesmo evento (deduplicação)
        assertThat(second.getEventId()).isEqualTo(first.getEventId());
        assertThat(queue.getQueueSize()).isEqualTo(1);
    }

    // ===== pollNext =====

    @Test
    @DisplayName("dado_filaComEvento_quando_pollNextNonBlocking_entao_retornaEvento")
    void testGivenQueueWithEvent_WhenPollNextNonBlocking_ThenReturnsEvent() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );
        queue.enqueueEvent(event);

        TenantMigrationEvent polled = queue.pollNextNonBlocking();

        assertThat(polled).isNotNull();
        assertThat(polled.getEventId()).isEqualTo(event.getEventId());
    }

    @Test
    @DisplayName("dado_filaVazia_quando_pollNextNonBlocking_entao_retornaNull")
    void testGivenEmptyQueue_WhenPollNextNonBlocking_ThenReturnsNull() {
        TenantMigrationEvent result = queue.pollNextNonBlocking();
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("dado_filaComEvento_quando_pollNextComTimeout_entao_retornaEvento")
    void testGivenQueueWithEvent_WhenPollNextWithTimeout_ThenReturnsEvent() throws InterruptedException {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );
        queue.enqueueEvent(event);

        TenantMigrationEvent polled = queue.pollNext(100);

        assertThat(polled).isNotNull();
    }

    @Test
    @DisplayName("dado_filaVazia_quando_pollNextComTimeout_entao_retornaNullAposTimeout")
    void testGivenEmptyQueue_WhenPollNextWithTimeout_ThenReturnsNullAfterTimeout() throws InterruptedException {
        TenantMigrationEvent result = queue.pollNext(50);
        assertThat(result).isNull();
    }

    // ===== hasPendingMigration =====

    @Test
    @DisplayName("dado_tenantComEventoPending_quando_hasPendingMigration_entao_retornaTrue")
    void testGivenTenantWithPendingEvent_WhenHasPendingMigration_ThenReturnsTrue() {
        queue.enqueueEvent(1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP);

        assertThat(queue.hasPendingMigration(1L)).isTrue();
    }

    @Test
    @DisplayName("dado_tenantSemEventoPending_quando_hasPendingMigration_entao_retornaFalse")
    void testGivenTenantWithoutPendingEvent_WhenHasPendingMigration_ThenReturnsFalse() {
        assertThat(queue.hasPendingMigration(999L)).isFalse();
    }

    // ===== getPendingMigrationForTenant =====

    @Test
    @DisplayName("dado_tenantComEventoPending_quando_getPendingMigrationForTenant_entao_retornaEvento")
    void testGivenTenantWithPendingEvent_WhenGetPendingMigration_ThenReturnsEvent() {
        queue.enqueueEvent(1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP);

        Optional<TenantMigrationEvent> result = queue.getPendingMigrationForTenant(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTenantId()).isEqualTo(1L);
    }

    // ===== enqueueForRetry =====

    @Test
    @DisplayName("dado_eventoComRetryDisponivel_quando_enqueueForRetry_entao_reEnfileira")
    void testGivenEventWithRetryAvailable_WhenEnqueueForRetry_ThenReEnqueues() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );
        queue.enqueueEvent(event);
        queue.pollNextNonBlocking(); // Remover da fila

        queue.enqueueForRetry(event);

        assertThat(event.getRetryCount()).isEqualTo(1);
        assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.PENDING);
        assertThat(queue.getQueueSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("dado_eventoComMaxRetries_quando_enqueueForRetry_entao_marcaComoFailed")
    void testGivenEventWithMaxRetries_WhenEnqueueForRetry_ThenMarksFailed() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );
        event.markStarted();
        event.markFailed("Error");

        // Simular que já atingiu maxRetries (3)
        event.markForRetry(); // 1
        event.markForRetry(); // 2
        event.markForRetry(); // 3

        queue.enqueueForRetry(event);

        assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.FAILED);
    }

    // ===== recordEventCompletion =====

    @Test
    @DisplayName("dado_eventoProcessado_quando_recordEventCompletion_entao_adicionaHistorico")
    void testGivenProcessedEvent_WhenRecordCompletion_ThenAddsToHistory() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );
        event.markStarted();
        event.markSuccess(1, 2);

        queue.recordEventCompletion(event);

        Collection<TenantMigrationEvent> completed = queue.getCompletedEvents();
        assertThat(completed).hasSize(1);
    }

    // ===== getStats =====

    @Test
    @DisplayName("dado_filaComMultiplosEventos_quando_getStats_entao_retornaEstatisticas")
    void testGivenQueueWithMultipleEvents_WhenGetStats_ThenReturnsStats() {
        // Enfileirar 2 eventos
        queue.enqueueEvent(1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP);
        queue.enqueueEvent(2L, "Tenant B", null, MigrationEventSource.APPLICATION_STARTUP);

        TenantMigrationQueue.MigrationQueueStats stats = queue.getStats();

        assertThat(stats.getTotalEvents()).isEqualTo(2);
        assertThat(stats.getPendingEvents()).isEqualTo(2);
        assertThat(stats.getCompletedEvents()).isEqualTo(0);
        assertThat(stats.getQueueSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("dado_filaVazia_quando_getStats_entao_progress100")
    void testGivenEmptyQueue_WhenGetStats_ThenProgress100() {
        TenantMigrationQueue.MigrationQueueStats stats = queue.getStats();

        assertThat(stats.getTotalEvents()).isEqualTo(0);
        assertThat(stats.getProgress()).isEqualTo(100.0);
        assertThat(stats.getSuccessRate()).isEqualTo(100.0);
    }

    // ===== Filters =====

    @Test
    @DisplayName("dado_eventosComDiferentesStatus_quando_getPendingEvents_entao_retornaApenasPending")
    void testGivenEventsWithDifferentStatus_WhenGetPendingEvents_ThenReturnsPendingOnly() {
        TenantMigrationEvent e1 = queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        TenantMigrationEvent e2 = queue.enqueueEvent(2L, "B", null, MigrationEventSource.APPLICATION_STARTUP);

        e1.markStarted(); // IN_PROGRESS

        Collection<TenantMigrationEvent> pending = queue.getPendingEvents();
        assertThat(pending).hasSize(1);
    }

    @Test
    @DisplayName("dado_eventosComFalha_quando_getFailedEvents_entao_retornaApenasFailed")
    void testGivenFailedEvents_WhenGetFailedEvents_ThenReturnsOnlyFailed() {
        TenantMigrationEvent e1 = queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        e1.markStarted();
        e1.markFailed("Error");
        queue.recordEventCompletion(e1);

        Collection<TenantMigrationEvent> failed = queue.getFailedEvents();
        assertThat(failed).hasSize(1);
    }

    // ===== Clear =====

    @Test
    @DisplayName("dado_filaComEventos_quando_clear_entao_filaVazia")
    void testGivenQueueWithEvents_WhenClear_ThenQueueEmpty() {
        queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        queue.enqueueEvent(2L, "B", null, MigrationEventSource.APPLICATION_STARTUP);

        queue.clear();

        assertThat(queue.isQueueEmpty()).isTrue();
        assertThat(queue.getQueueSize()).isEqualTo(0);
    }

    // ===== getQueueSize / isQueueEmpty =====

    @Test
    @DisplayName("dado_filaVaziaInicial_quando_isQueueEmpty_entao_retornaTrue")
    void testGivenInitialEmptyQueue_WhenIsQueueEmpty_ThenReturnsTrue() {
        assertThat(queue.isQueueEmpty()).isTrue();
        assertThat(queue.getQueueSize()).isEqualTo(0);
    }

    // ===== getEventsByTenant =====

    @Test
    @DisplayName("dado_multiplosTenants_quando_getEventsByTenant_entao_filtraPorTenant")
    void testGivenMultipleTenants_WhenGetEventsByTenant_ThenFiltersByTenant() {
        queue.enqueueEvent(1L, "A", null, MigrationEventSource.APPLICATION_STARTUP);
        queue.enqueueEvent(2L, "B", null, MigrationEventSource.APPLICATION_STARTUP);

        Collection<TenantMigrationEvent> events = queue.getEventsByTenant(1L);
        assertThat(events).hasSize(1);
        assertThat(events.iterator().next().getTenantId()).isEqualTo(1L);
    }

    // ===== getMaxRetries / getRetryDelayMs =====

    @Test
    @DisplayName("dado_queue_quando_getMaxRetries_entao_retorna3")
    void testGivenQueue_WhenGetMaxRetries_ThenReturns3() {
        assertThat(queue.getMaxRetries()).isEqualTo(3);
    }

    @Test
    @DisplayName("dado_queue_quando_getRetryDelayMs_entao_retorna5000")
    void testGivenQueue_WhenGetRetryDelayMs_ThenReturns5000() {
        assertThat(queue.getRetryDelayMs()).isEqualTo(5000);
    }
}
