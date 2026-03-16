package com.api.erp.v1.main.master.tenant.infrastructure.config;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantFeature;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantFeatureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TenantBeanValidation
 * <p>
 * Valida na startup se todos os beans configurados em tb_tnt_features existem no contexto Spring.
 * <p>
 * Falha rápido se encontrar configurações inválidas, evitando surpresas em runtime.
 */
@Component
@Slf4j
public class TenantBeanValidation {

    private final TenantFeatureRepository featureRepository;
    private final ApplicationContext applicationContext;

    public TenantBeanValidation(
            TenantFeatureRepository featureRepository,
            ApplicationContext applicationContext) {
        this.featureRepository = featureRepository;
        this.applicationContext = applicationContext;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void validateTenantBeansOnStartup() {
        log.info("[TenantBeanValidation] Iniciando validação de beans configurados em tb_tnt_features...");

        List<TenantFeature> features = featureRepository.findAll();

        if (features.isEmpty()) {
            log.info("[TenantBeanValidation] Nenhuma feature configurada em tb_tnt_features");
            return;
        }

        int validCount = 0;
        int invalidCount = 0;

        for (TenantFeature feature : features) {
            if (!feature.getActive()) {
                log.debug("[TenantBeanValidation] Feature inativa, pulando: tenant={}, feature={}", 
                          feature.getTenant().getId(), feature.getFeatureKey());
                continue;
            }

            String beanName = feature.getBeanName();
            try {
                Object bean = applicationContext.getBean(beanName);
                validCount++;
                log.debug("[TenantBeanValidation] ✓ Bean válido: {} (tenant={}, feature={})", 
                          beanName, feature.getTenant().getId(), feature.getFeatureKey());
            } catch (Exception e) {
                invalidCount++;
                String errorMsg = String.format(
                        "Bean '%s' configurado em tb_tnt_features não existe! " +
                        "(Tenant=%d, Feature='%s')", 
                        beanName, feature.getTenant().getId(), feature.getFeatureKey());
                log.error("[TenantBeanValidation] ✗ {}", errorMsg);
                log.error("[TenantBeanValidation] Causa: {}", e.getMessage());
            }
        }

        log.info("[TenantBeanValidation] Validação concluída: {} válidos, {} inválidos", 
                 validCount, invalidCount);

        if (invalidCount > 0) {
            throw new IllegalStateException(
                    String.format(
                            "Configuração inválida em tb_tnt_features: %d beans não encontrados no contexto Spring. " +
                            "Revise as configurações e garanta que todos os bean_names existem.",
                            invalidCount));
        }
    }
}
