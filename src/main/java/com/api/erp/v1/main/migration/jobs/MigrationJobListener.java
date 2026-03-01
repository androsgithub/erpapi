//package com.api.erp.v1.main.migration.jobs;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.job.JobExecution;
//import org.springframework.batch.core.listener.JobExecutionListener;
//import org.springframework.batch.core.step.StepExecution;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
///**
// * LISTENER - Eventos do Job de Migração
// *
// * Registra eventos de início e término do job de migrações,
// * fornecendo informações detalhadas sobre a execução.
// *
// * @author ERP System
// * @version 1.0
// */
//@Slf4j
//public class MigrationJobListener implements JobExecutionListener {
//
//    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//
//    /**
//     * Executado antes do job iniciar
//     *
//     * Registra informações sobre o job que será executado
//     */
//    @Override
//    public void beforeJob(JobExecution jobExecution) {
//        log.info("");
//        log.info("╔════════════════════════════════════════════════════════════════╗");
//        log.info("║         INICIANDO SPRING BATCH - MIGRAÇÕES DE TENANTS          ║");
//        log.info("╣════════════════════════════════════════════════════════════════╣");
//        log.info("║ Job ID:          {}", padRight(String.valueOf(jobExecution.getId()), 48));
//        log.info("║ Job Name:        {}", padRight(jobExecution.getJobInstance().getJobName(), 48));
//        log.info("║ Start Time:      {}", padRight(formatDateTime(jobExecution.getStartTime()), 48));
//        log.info("║ Status:          {}", padRight(jobExecution.getStatus().toString(), 48));
//        log.info("╚════════════════════════════════════════════════════════════════╝");
//        log.info("");
//    }
//
//    /**
//     * Executado após o job finalizar
//     *
//     * Registra estatísticas completas da execução
//     */
//    @Override
//    public void afterJob(JobExecution jobExecution) {
//        long durationSeconds = Duration.between(
//                jobExecution.getStartTime(),
//                jobExecution.getEndTime()
//        ).getSeconds();
//
//        long totalItems = jobExecution.getStepExecutions().stream()
//                .mapToLong(StepExecution::getCommitCount)
//                .sum();
//
//        long successItems = jobExecution.getStepExecutions().stream()
//                .mapToLong(StepExecution::getWriteCount)
//                .sum();
//
//        long skipItems = jobExecution.getStepExecutions().stream()
//                .mapToLong(StepExecution::getSkipCount)
//                .sum();
//
//        log.info("");
//        log.info("╔════════════════════════════════════════════════════════════════╗");
//        log.info("║         FIM DO SPRING BATCH - RESULTS DA EXECUÇÃO              ║");
//        log.info("╣════════════════════════════════════════════════════════════════╣");
//        log.info("║ Status Final:    {}", padRight(jobExecution.getStatus().toString(), 48));
//        log.info("║ End Time:        {}", padRight(formatDateTime(jobExecution.getEndTime()), 48));
//        log.info("║ Duração:         {}", padRight(durationSeconds + "s", 48));
//        log.info("║", "");
//        log.info("║ 📊 ESTATÍSTICAS:");
//        log.info("║    ✅ Itens processados:    {}", successItems);
//        log.info("║    ⏭️  Itens pulados:       {}", skipItems);
//        log.info("║    ❌ Erros totais:        {}", jobExecution.getAllFailureExceptions().size());
//        log.info("║", "");
//
//        // Log de erros se houver
//        if (!jobExecution.getAllFailureExceptions().isEmpty()) {
//            log.error("║ ERROS ENCONTRADOS:");
//            jobExecution.getAllFailureExceptions().stream()
//                    .limit(5)  // Mostra até 5 erros
//                    .forEach(e -> log.error("║    - {}", e.getMessage()));
//
//            if (jobExecution.getAllFailureExceptions().size() > 5) {
//                log.error("║    ... e mais {} erro(s)", jobExecution.getAllFailureExceptions().size() - 5);
//            }
//        }
//
//        log.info("╚════════════════════════════════════════════════════════════════╝");
//        log.info("");
//
//        // Log de resumo colorido
//        if (jobExecution.getStatus().isUnsuccessful()) {
//            log.warn("⚠️ JOB FINALIZADO COM FALHAS - Verifique os erros acima");
//        } else {
//            log.info("✅ JOB FINALIZADO COM SUCESSO");
//        }
//        log.info("");
//    }
//
//    /**
//     * Formata LocalDateTime para String legível
//     */
//    private String formatDateTime(LocalDateTime dateTime) {
//        if (dateTime == null) return "N/A";
//        return dateTime.format(dateFormatter);
//    }
//
//    /**
//     * Preenche string com espaços à direita até atingir o tamanho desejado
//     */
//    private String padRight(String str, int size) {
//        if (str.length() >= size) {
//            return str.substring(0, size - 3) + "...";
//        }
//        return str + " ".repeat(size - str.length());
//    }
//}
