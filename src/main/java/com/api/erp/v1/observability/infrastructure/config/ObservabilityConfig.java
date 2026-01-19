package com.api.erp.v1.observability.infrastructure.config;

import com.api.erp.v1.observability.application.registry.StepRegistry;
import com.api.erp.v1.observability.domain.FlowTracker;
import com.api.erp.v1.observability.infrastructure.composite.CompositeFlowTracker;
import com.api.erp.v1.observability.infrastructure.db.DatabaseFlowTracker;
import com.api.erp.v1.observability.infrastructure.messaging.KafkaFlowTracker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuração central do módulo de observabilidade.
 * 
 * Responsabilidades:
 * - Ativar processamento assíncrono (@EnableAsync)
 * - Registrar components de tracking (DatabaseFlowTracker, KafkaFlowTracker)
 * - Montar o CompositeFlowTracker para delegar eventos
 * 
 * IMPORTANTE: @EnableJpaRepositories foi removido e movido para LogsRepositoriesConfig
 * para evitar conflitos com outros repositórios na aplicação.
 */
@Configuration
@EnableAsync
public class ObservabilityConfig {
    @Bean
    public StepRegistry stepRegistry() {
        return new StepRegistry();
    }

    @Bean
    public CompositeFlowTracker compositeFlowTracker(
            DatabaseFlowTracker databaseTracker,
            KafkaFlowTracker kafkaTracker) {

        CompositeFlowTracker composite = new CompositeFlowTracker();

        // Registra o tracker de banco de dados
        composite.register(databaseTracker);

        // Registra o tracker de Kafka
        composite.register(kafkaTracker);

        return composite;
    }

    @Bean
    public FlowTracker flowTracker(CompositeFlowTracker composite) {
        return composite;
    }
}
