package com.api.erp.v1.main.migration.jobs;

import com.api.erp.v1.main.tenant.domain.entity.TenantDatasource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * INFRASTRUCTURE - Configuração do Spring Batch para Migrações
 *
 * Define:
 * - Job: tenantMigrationJob
 * - Step: migrationStep (chunk-based, processa tenants em lotes)
 * - Reader: TenantMigrationReader (lê datasources ativos)
 * - Processor: TenantMigrationProcessor (executa migração + seed)
 * - Writer: TenantMigrationWriter (consolida resultados)
 * - JobLauncher: SimpleJobLauncher (dispara o job)
 *
 * Características:
 * - Chunk size: 5 tenants por vez
 * - Fault Tolerance: pula tenants com erro, continua o processamento
 * - Transação: gerenciada pelo Spring
 *
 * @author ERP System
 * @version 1.0
 */
@Configuration
@Slf4j
public class MigrationJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TenantMigrationReader reader;
    private final TenantMigrationProcessor processor;
    private final TenantMigrationWriter writer;

    @Autowired
    public MigrationJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, TenantMigrationReader reader, TenantMigrationProcessor processor, TenantMigrationWriter writer) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
    }

    /**
     * Define o Job principal de migrações
     *
     * Executa sequencialmente:
     * 1. MigrationJobListener.beforeJob (logs iniciais)
     * 2. migrationStep (processamento em chunks)
     * 3. MigrationJobListener.afterJob (logs finais)
     */
    @Bean
    public Job tenantMigrationJob(Step migrationStep) {
        return new JobBuilder("tenantMigrationJob", jobRepository)
//                .listener(tenantMigrationJobListener())
                .preventRestart()
                .start(migrationStep)
                .build();
    }

    /**
     * Define o Step que processa tenants em chunks
     *
     * Características:
     * - Chunk size: 5 tenants de uma vez
     * - Transação controlada por chunk
     * - Fault tolerance: salta erros, continua processamento
     */
    @Bean
    public Step migrationStep() {
        return new StepBuilder("migrationStep", jobRepository)
                .<TenantDatasource, MigrationResult>chunk(25)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(100)
                .build();
    }

    /**
     * Bean do listener com logs estruturados
     */
//    @Bean
//    public MigrationJobListener tenantMigrationJobListener() {
//        return new MigrationJobListener();
//    }

    /**
     * JobLauncher com configuração simples e síncrona
     *
     * A execução assíncrona vem de @Async em MigrationJobLauncher
     */
    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(new SyncTaskExecutor());
        launcher.afterPropertiesSet();
        return launcher;
    }
}
