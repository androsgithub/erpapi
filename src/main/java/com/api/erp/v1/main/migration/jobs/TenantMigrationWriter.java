package com.api.erp.v1.main.migration.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WRITER - Consolidação de Resultados de Migração
 * 
 * Responsável por:
 * 1. Consolidar resultados de um lote (chunk) de migrações
 * 2. Registrar sucessos e falhas
 * 3. Fornecer relatório por lote
 * 
 * Características:
 * - Agrupa resultados de 5 tenants por escritura
 * - Log detalhado de sucessos e falhas
 * - Estatísticas por lote
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@Slf4j
public class TenantMigrationWriter implements ItemWriter<MigrationResult> {

    /**
     * Escreve/consolida os resultados de um lote de migrações
     * 
     * Mantém histórico de:
     * - Quais tenants foram migrados com sucesso
     * - Quais falharam e por quê
     * - Estatísticas do lote
     */
    @Override
    public void write(Chunk<? extends MigrationResult> results) {
        List<? extends MigrationResult> items = results.getItems();

        if (items.isEmpty()) {
            log.warn("⚠️ Write chamado com lote vazio");
            return;
        }

        // Separa sucessos e falhas
        long successCount = items.stream()
                .filter(MigrationResult::isSuccess)
                .count();

        long failureCount = items.stream()
                .filter(r -> !r.isSuccess())
                .count();

        // Log de consolidação por lote
        log.info("");
        log.info("┌─────────────────────────────────────────────────────────────┐");
        log.info("│ CONSOLIDAÇÃO DE LOTE (CHUNK)                                │");
        log.info("├─────────────────────────────────────────────────────────────┤");
        log.info("│ Total de itens: {}                                            │", String.format("%-33s", items.size()));
        log.info("│", "");

        // Log de sucessos
        if (successCount > 0) {
            log.info("│ ✅ Sucessos: {}                                             │", String.format("%-35s", successCount));
            items.stream()
                    .filter(MigrationResult::isSuccess)
                    .forEach(r -> log.info("│    • Tenant ID: {}                                         │", String.format("%-34s", r.getTenantId())));
        }

        // Log de falhas
        if (failureCount > 0) {
            log.error("│", "");
            log.error("│ ❌ Falhas: {}                                              │", String.format("%-36s", failureCount));

            items.stream()
                    .filter(r -> !r.isSuccess())
                    .forEach(r -> {
                        log.error("│    • Tenant ID: {}                                         │", String.format("%-34s", r.getTenantId()));
                        log.error("│      Motivo: {}                    │", 
                                truncateString(r.getErrorMessage(), 49));
                    });
        }

        log.info("│", "");
        log.info("└─────────────────────────────────────────────────────────────┘");
        log.info("");

        double successPercent = (successCount * 100.0) / items.size();
        double failurePercent = (failureCount * 100.0) / items.size();

        log.info("📊 Estatísticas do Lote:");
        log.info("   ✅ Sucessos: {} ({}%)",
                successCount,
                String.format("%.1f", successPercent));
        log.info("   ❌ Falhas:   {} ({}%)",
                failureCount,
                String.format("%.1f", failurePercent));
    }

    /**
     * Trunca string para não ultrapassar tamanho máximo
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return "Unknown error";
        }
        if (str.length() <= maxLength) {
            return str + " ".repeat(maxLength - str.length());
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}