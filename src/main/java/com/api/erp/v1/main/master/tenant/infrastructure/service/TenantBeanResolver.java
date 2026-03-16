package com.api.erp.v1.main.master.tenant.infrastructure.service;

import com.api.erp.v1.main.master.tenant.domain.entity.TenantFeature;
import com.api.erp.v1.main.master.tenant.domain.repository.TenantFeatureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * TenantBeanResolver
 * <p>
 * Componente central para resolução de beans por tenant com override via tb_tnt_features.
 * <p>
 * Fluxo:
 * 1. Verifica se existe override ativo em tb_tnt_features para (tenantId, featureKey)
 * 2. Se existir, recupera o bean_name e resolve do ApplicationContext
 * 3. Se não existir, usa o bean @Primary do tipo solicitado (fallback)
 * 4. Cache em Redis para evitar lookup repetido
 * <p>
 * Exceções:
 * - TenantBeanNotFoundException: bean configurado não existe no contexto
 * - TenantFeatureNotFoundException: nenhuma feature configurada e sem @Primary disponível
 */
@Component
@Slf4j
public class TenantBeanResolver {

    private final TenantFeatureRepository featureRepository;
    private final ApplicationContext applicationContext;

    public TenantBeanResolver(
            TenantFeatureRepository featureRepository,
            ApplicationContext applicationContext) {
        this.featureRepository = featureRepository;
        this.applicationContext = applicationContext;
    }

    /**
     * Resolve o bean para um tenant e feature específica
     * <p>
     * Prioridade:
     * 1. Bean customizado via tb_tnt_features (se ativo)
     * 2. Bean @Primary do tipo solicitado (fallback)
     *
     * @param tenantId   ID do tenant
     * @param featureKey chave da feature (ex: "userService", "invoiceService")
     * @param type       classe do bean esperado
     * @param <T>        tipo genérico
     * @return instância do bean resolvido
     * @throws TenantBeanNotFoundException se bean_name configurado não existe
     * @throws TenantFeatureNotFoundException se nenhum override existe e sem @Primary disponível
     */
    @Cacheable(
            value = "tenant-features",
            key = "#tenantId + ':' + #featureKey + ':' + #type.simpleName",
            cacheManager = "cacheManager" // Redis via Spring Cache
    )
    public <T> T resolve(Long tenantId, String featureKey, Class<T> type) {
        log.debug("[TenantBeanResolver] Resolvendo bean para tenant={}, featureKey={}, type={}", 
                  tenantId, featureKey, type.getSimpleName());

        // 1. Busca override em tb_tnt_features
        TenantFeature feature = featureRepository
                .findByTenantIdAndFeatureKeyAndActiveTrue(tenantId, featureKey)
                .orElse(null);

        if (feature != null) {
            String beanName = feature.getBeanName();
            log.debug("[TenantBeanResolver] Feature override encontrada: tenant={}, feature={}, bean={}", 
                      tenantId, featureKey, beanName);

            try {
                T bean = applicationContext.getBean(beanName, type);
                log.debug("[TenantBeanResolver] Bean resolvido via override: {}", beanName);
                return bean;
            } catch (NoSuchBeanDefinitionException e) {
                String errorMsg = String.format(
                        "Bean '%s' configurado em tb_tnt_features não existe no contexto Spring. " +
                        "Tenant=%d, Feature='%s'", 
                        beanName, tenantId, featureKey);
                log.error("[TenantBeanResolver] {}", errorMsg);
                throw new TenantBeanNotFoundException(errorMsg, e);
            }
        }

        // 2. Fallback: busca bean @Primary
        log.debug("[TenantBeanResolver] Nenhum override encontrado, usando fallback @Primary");
        try {
            T bean = applicationContext.getBean(type);
            log.debug("[TenantBeanResolver] Bean resolvido via @Primary: {}", type.getSimpleName());
            return bean;
        } catch (NoSuchBeanDefinitionException e) {
            String errorMsg = String.format(
                    "Nenhum bean @Primary disponível para tipo '%s'. " +
                    "Tenant=%d, Feature='%s'. Configure tb_tnt_features ou defina um @Primary.", 
                    type.getSimpleName(), tenantId, featureKey);
            log.error("[TenantBeanResolver] {}", errorMsg);
            throw new TenantFeatureNotFoundException(errorMsg, e);
        }
    }

    /**
     * Versão alternativa que resolve pelo nome se a classe não estiver imediatamente disponível
     * (útil para casos onde o tipo é genérico ou desconhecido em tempo de compilação)
     *
     * @param tenantId   ID do tenant
     * @param featureKey chave da feature
     * @param typeName   nome completo da classe (ex: "com.api.erp.v1.main.features.user.domain.service.IUserService")
     * @return instância do bean como Object
     */
    @Cacheable(
            value = "tenant-features",
            key = "#tenantId + ':' + #featureKey + ':' + #typeName",
            cacheManager = "cacheManager"
    )
    public Object resolveByTypeName(Long tenantId, String featureKey, String typeName) {
        log.debug("[TenantBeanResolver] Resolvendo bean por typeName: tenant={}, featureKey={}, typeName={}", 
                  tenantId, featureKey, typeName);

        try {
            Class<?> type = Class.forName(typeName);
            return resolve(tenantId, featureKey, type);
        } catch (ClassNotFoundException e) {
            String errorMsg = String.format("Classe '%s' não encontrada", typeName);
            log.error("[TenantBeanResolver] {}", errorMsg);
            throw new TenantFeatureNotFoundException(errorMsg, e);
        }
    }

    /**
     * Exceção lançada quando o bean configurado não existe no contexto Spring
     */
    public static class TenantBeanNotFoundException extends RuntimeException {
        public TenantBeanNotFoundException(String message) {
            super(message);
        }

        public TenantBeanNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exceção lançada quando nenhuma feature está configurada e sem @Primary disponível
     */
    public static class TenantFeatureNotFoundException extends RuntimeException {
        public TenantFeatureNotFoundException(String message) {
            super(message);
        }

        public TenantFeatureNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
