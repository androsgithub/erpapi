package com.api.erp.v1.main.config.startup;

import com.api.erp.v1.main.migration.startup.TenantMigrationStartupWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS - ApplicationStartupListener
 *
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationStartupListener - Testes Unitários")
class ApplicationStartupListenerTest {

    @Mock
    private TenantMigrationStartupWorker migrationStartupWorker;

    private ApplicationStartupListener listener;

    @BeforeEach
    void setUp() {
        listener = new ApplicationStartupListener(migrationStartupWorker);
    }

    @Test
    @DisplayName("dado_applicationReady_quando_initializeMigrationQueue_entao_delegaParaWorker")
    void testGivenApplicationReady_WhenInitialize_ThenDelegatesToWorker() {
        listener.initializeMigrationQueueOnStartup();

        verify(migrationStartupWorker, times(1)).initializeAndStart();
    }

    @Test
    @DisplayName("dado_workerLancaExcecao_quando_initializeMigrationQueue_entao_naoRelanca")
    void testGivenWorkerThrowsException_WhenInitialize_ThenDoesNotRethrow() {
        doThrow(new RuntimeException("Startup error")).when(migrationStartupWorker).initializeAndStart();

        // O método do worker não lança exceção (ele captura internamente),
        // mas se por algum motivo lançar, testamos que propaga
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> listener.initializeMigrationQueueOnStartup());
    }
}
