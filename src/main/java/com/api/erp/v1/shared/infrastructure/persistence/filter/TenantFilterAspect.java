package com.api.erp.v1.shared.infrastructure.persistence.filter;

import com.api.erp.v1.tenant.infrastructure.config.datasource.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * TenantFilterAspect
 *
 * Aplica filtro de tenantId ANTES de QUALQUER operação de Repository.
 * Isso garante que o filtro está ativo no momento EXATO em que a query é executada.
 *
 * ✅ Vantagem: Mais confiável que Interceptor pois intercepta no nível da Repository
 * ✅ Garante que o filtro está ativo durante a execução da query
 * ❌ Desvantagem: Precisa que TenantContext já esteja setado
 */
@Slf4j
@Aspect
@Component
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String TENANT_ID_FILTER = "tenantIdFilter";
    private static final String TENANT_ID_PARAM = "tenantId";

    /**
     * Intercepta QUALQUER método de Repository antes de sua execução.
     * Ativa o filtro se houver tenantId no context.
     */
    @Around("execution(* com.api.erp.v1..repository..*Repository.*(..))")
    public Object applyTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Long tenantId = TenantContext.getTenantId();
        
        if (tenantId != null) {
            try {
                Session session = entityManager.unwrap(Session.class);
                
                // ✅ ATIVA O FILTRO EXATAMENTE ANTES DA QUERY
                Filter filter = session.enableFilter(TENANT_ID_FILTER);
                filter.setParameter(TENANT_ID_PARAM, tenantId);
                
                log.debug("🔐 [TenantFilterAspect] Filtro ATIVADO no nível de Repository | tenantId: {} | método: {}", 
                        tenantId, joinPoint.getSignature().getName());
                
                try {
                    // Executa a query COM o filtro ativo
                    return joinPoint.proceed();
                } finally {
                    // ✅ DESATIVA após a execução
                    session.disableFilter(TENANT_ID_FILTER);
                    log.debug("🔓 [TenantFilterAspect] Filtro desativado após Repository");
                }
                
            } catch (Exception e) {
                log.warn("⚠️ [TenantFilterAspect] Erro ao aplicar filtro de tenantId", e);
                return joinPoint.proceed();
            }
        } else {
            log.debug("⚠️ [TenantFilterAspect] TenantId é null, filtro NÃO aplicado");
            return joinPoint.proceed();
        }
    }
}
