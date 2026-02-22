package com.api.erp.v1.main.config.startup.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * COMPONENT - Coordenador de Seeders de Inicialização
 * 
 * Orquestra a execução de todos os seeders em ordem correta.
 * Cada seeder é responsável por inicializar dados específicos de um módulo.
 * 
 * Responsabilidades:
 * - Executar permissões antes de usuários
 * - Executar usuários antes de unidades
 * - Tratamento centralizado de erros
 * - Logging de progresso
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MainSeed {

    private final PermissionSeed permissionSeed;
    private final UserAdminSeed userAdminSeed;
    private final MeasureUnitSeed measureUnitSeed;

    public void executar() {
        log.info("📋 Iniciando execução de seeders de inicialização...");
        int sucessos = 0;
        int erros = 0;

        try {
            log.debug("1️⃣  Executando seeder de permissões...");
            permissionSeed.executar();
            log.info("✅ Permissões inicializadas com sucesso");
            sucessos++;
        } catch (Exception e) {
            log.error("❌ Erro ao executar seeder de permissões:", e);
            erros++;
        }

        try {
            log.debug("2️⃣  Executando seeder de usuário admin...");
            userAdminSeed.executar();
            log.info("✅ Usuário admin inicializado com sucesso");
            sucessos++;
        } catch (Exception e) {
            log.error("❌ Erro ao executar seeder de usuário admin:", e);
            erros++;
        }

        // ❌ COMENTADO: Unidades de medida agora são migração FlywayDB
        // - Usar migration: V2__Insert_Measure_Units.sql para dados padrão
        // - Seed apenas para unidades específicas do tenant se necessário
        // try {
        //     log.debug("3️⃣  Executando seeder de unidades de medida...");
        //     measureUnitSeed.executar();
        //     log.info("✅ Unidades de medida inicializadas com sucesso");
        //     sucessos++;
        // } catch (Exception e) {
        //     log.error("❌ Erro ao executar seeder de unidades de medida:", e);
        //     erros++;
        // }

        if (erros == 0) {
            log.info("✅ Todos os {} seeders foram executados com sucesso", sucessos);
        } else {
            log.warn("⚠️  Execução de seeders concluída: {} sucesso(s), {} erro(s)", sucessos, erros);
        }
    }
}
