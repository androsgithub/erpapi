package com.api.erp.v1.main.migration.domain;

import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventResult;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventSource;
import com.api.erp.v1.main.migration.domain.TenantMigrationEvent.MigrationEventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - TenantMigrationEvent
 *
 * Cobre lifecycle: create → markStarted → markSuccess/markFailed → markForRetry
 *
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("TenantMigrationEvent - Testes Unitários")
class TenantMigrationEventTest {

    // ===== Factory Method =====

    @Test
    @DisplayName("dado_dadosValidos_quando_create_entao_eventoCriadoComStatusPending")
    void testGivenValidData_WhenCreate_ThenEventCreatedWithPendingStatus() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        assertThat(event.getEventId()).isNotNull().isNotBlank();
        assertThat(event.getTenantId()).isEqualTo(1L);
        assertThat(event.getTenantName()).isEqualTo("Tenant A");
        assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.PENDING);
        assertThat(event.getEnqueuedAt()).isNotNull();
        assertThat(event.getSource()).isEqualTo(MigrationEventSource.APPLICATION_STARTUP);
        assertThat(event.getRetryCount()).isEqualTo(0);
        assertThat(event.getStartedAt()).isNull();
        assertThat(event.getCompletedAt()).isNull();
        assertThat(event.getResult()).isNull();
        assertThat(event.getErrorMessage()).isNull();
    }

    @Test
    @DisplayName("dado_sourceTenantCreation_quando_create_entao_sourceTenantCreation")
    void testGivenTenantCreationSource_WhenCreate_ThenSourceIsTenantCreation() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                2L, "Tenant B", null, MigrationEventSource.TENANT_CREATION
        );

        assertThat(event.getSource()).isEqualTo(MigrationEventSource.TENANT_CREATION);
    }

    // ===== markStarted =====

    @Test
    @DisplayName("dado_eventoPending_quando_markStarted_entao_statusInProgress")
    void testGivenPendingEvent_WhenMarkStarted_ThenStatusInProgress() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        event.markStarted();

        assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.IN_PROGRESS);
        assertThat(event.getStartedAt()).isNotNull();
    }

    // ===== markSuccess =====

    @Test
    @DisplayName("dado_eventoInProgress_quando_markSuccess_entao_statusCompletedComTempo")
    void testGivenInProgressEvent_WhenMarkSuccess_ThenStatusCompletedWithTime() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );
        event.markStarted();

        event.markSuccess(5, 2);

        assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.COMPLETED);
        assertThat(event.getResult()).isEqualTo(MigrationEventResult.SUCCESS);
        assertThat(event.getCompletedAt()).isNotNull();
        assertThat(event.getMigrationsExecuted()).isEqualTo(5);
        assertThat(event.getSeedersExecuted()).isEqualTo(2);
        assertThat(event.getExecutionTimeMs()).isGreaterThanOrEqualTo(0);
    }

    // ===== markFailed =====

    @Test
    @DisplayName("dado_eventoInProgress_quando_markFailed_entao_statusFailedComMensagem")
    void testGivenInProgressEvent_WhenMarkFailed_ThenStatusFailedWithMessage() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );
        event.markStarted();

        event.markFailed("Connection refused");

        assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.FAILED);
        assertThat(event.getResult()).isEqualTo(MigrationEventResult.FAILURE);
        assertThat(event.getErrorMessage()).isEqualTo("Connection refused");
        assertThat(event.getCompletedAt()).isNotNull();
    }

    // ===== markForRetry =====

    @Test
    @DisplayName("dado_eventoFailed_quando_markForRetry_entao_statusPendingERetryIncrementado")
    void testGivenFailedEvent_WhenMarkForRetry_ThenStatusPendingAndRetryIncremented() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );
        event.markStarted();
        event.markFailed("Error");

        event.markForRetry();

        assertThat(event.getStatus()).isEqualTo(MigrationEventStatus.PENDING);
        assertThat(event.getRetryCount()).isEqualTo(1);
        assertThat(event.getStartedAt()).isNull();
        assertThat(event.getCompletedAt()).isNull();
        assertThat(event.getErrorMessage()).isNull();
        assertThat(event.getResult()).isNull();
    }

    @Test
    @DisplayName("dado_eventRetried2Vezes_quando_markForRetry_entao_retryCount3")
    void testGivenEventRetriedTwice_WhenMarkForRetry_ThenRetryCount3() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        event.markForRetry();
        event.markForRetry();
        event.markForRetry();

        assertThat(event.getRetryCount()).isEqualTo(3);
    }

    // ===== getWaitTimeMs =====

    @Test
    @DisplayName("dado_eventoComHorasEnfileiradoEIniciado_quando_getWaitTimeMs_entao_retornaTempoDeEspera")
    void testGivenEventWithEnqueuedAndStarted_WhenGetWaitTimeMs_ThenReturnsWaitTime() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        event.markStarted();
        long waitTime = event.getWaitTimeMs();

        assertThat(waitTime).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("dado_eventoSemIniciar_quando_getWaitTimeMs_entao_retorna0")
    void testGivenEventNotStarted_WhenGetWaitTimeMs_ThenReturns0() {
        TenantMigrationEvent event = TenantMigrationEvent.create(
                1L, "Tenant A", null, MigrationEventSource.APPLICATION_STARTUP
        );

        assertThat(event.getWaitTimeMs()).isEqualTo(0);
    }

    // ===== Enums Internos =====

    @Test
    @DisplayName("dado_statusTerminal_quando_isTerminal_entao_retornaTrue")
    void testGivenTerminalStatus_WhenIsTerminal_ThenReturnsTrue() {
        assertThat(MigrationEventStatus.COMPLETED.isTerminal()).isTrue();
        assertThat(MigrationEventStatus.FAILED.isTerminal()).isTrue();
        assertThat(MigrationEventStatus.CANCELLED.isTerminal()).isTrue();
    }

    @Test
    @DisplayName("dado_statusNaoTerminal_quando_isTerminal_entao_retornaFalse")
    void testGivenNonTerminalStatus_WhenIsTerminal_ThenReturnsFalse() {
        assertThat(MigrationEventStatus.PENDING.isTerminal()).isFalse();
        assertThat(MigrationEventStatus.IN_PROGRESS.isTerminal()).isFalse();
    }

    @Test
    @DisplayName("dado_statusEnum_quando_getLabel_entao_retornaLabel")
    void testGivenStatusEnum_WhenGetLabel_ThenReturnsLabel() {
        assertThat(MigrationEventStatus.PENDING.getLabel()).isNotNull();
        assertThat(MigrationEventStatus.IN_PROGRESS.getLabel()).isNotNull();
        assertThat(MigrationEventStatus.COMPLETED.getLabel()).isNotNull();
    }

    @Test
    @DisplayName("dado_resultEnum_quando_getLabel_entao_retornaLabel")
    void testGivenResultEnum_WhenGetLabel_ThenReturnsLabel() {
        assertThat(MigrationEventResult.SUCCESS.getLabel()).isNotNull();
        assertThat(MigrationEventResult.FAILURE.getLabel()).isNotNull();
    }

    @Test
    @DisplayName("dado_sourceEnum_quando_getLabel_entao_retornaLabel")
    void testGivenSourceEnum_WhenGetLabel_ThenReturnsLabel() {
        assertThat(MigrationEventSource.APPLICATION_STARTUP.getLabel()).isNotNull();
        assertThat(MigrationEventSource.TENANT_CREATION.getLabel()).isNotNull();
        assertThat(MigrationEventSource.MANUAL_REQUEST.getLabel()).isNotNull();
    }
}
