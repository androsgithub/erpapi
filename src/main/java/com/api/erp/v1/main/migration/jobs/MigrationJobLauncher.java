package com.api.erp.v1.main.migration.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * INFRASTRUCTURE - Disparador de Job de Migração
 * 
 * Responsável por disparar o Spring Batch Job de migrações
 * de forma assíncrona durante a inicialização da aplicação.
 * 
 * O Job é disparado com parâmetros únicos para permitir múltiplas
 * execuções e manter histórico no banco de dados.
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MigrationJobLauncher {

    private final JobLauncher jobLauncher;
    private final Job tenantMigrationJob;

    /**
     * Dispara o job de migração de tenants de forma assíncrona
     *
     * O job será executado em background, permitindo que a aplicação
     * continuar inicializando sem bloqueios.
     */
    @Async("migrationTaskExecutor")
    public void launchMigrationJob() {
        try {
            log.info("");
            log.info("╔════════════════════════════════════════════════════════════════╗");
            log.info("║     INICIANDO SPRING BATCH - MIGRAÇÕES DE TENANTS              ║");
            log.info("╚════════════════════════════════════════════════════════════════╝");
            log.info("");

            // Cria parâmetros únicos para cada execução do job
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("jobId", UUID.randomUUID().toString())
                    .addString("executionTimestamp", LocalDateTime.now().toString())
                    .toJobParameters();

            // Dispara o job de forma síncrona (dentro da thread assíncrona)
            var jobExecution = jobLauncher.run(tenantMigrationJob, jobParameters);

            // Log de resultado
            if (jobExecution.getStatus().isUnsuccessful()) {
                log.error("");
                log.error("╔════════════════════════════════════════════════════════════════╗");
                log.error("║      !!!   JOB DE MIGRAÇÃO FINALIZADO COM ERROS   !!!           ║");
                log.error("╚════════════════════════════════════════════════════════════════╝");
                jobExecution.getAllFailureExceptions().forEach(e ->
                        log.error("Erro: ", e)
                );
            }

        } catch (Exception e) {
            log.error("");
            log.error("╔════════════════════════════════════════════════════════════════╗");
            log.error("║   !!!   ERRO AO DISPARAR JOB DE MIGRAÇÃO   !!!                 ║");
            log.error("╚════════════════════════════════════════════════════════════════╝");
            log.error("Detalhes do erro: ", e);

            // Não relança exceção para permitir que a aplicação continue
            log.warn("⚠️ A aplicação continuará rodando apesar do erro na migração");
        }
    }
}
