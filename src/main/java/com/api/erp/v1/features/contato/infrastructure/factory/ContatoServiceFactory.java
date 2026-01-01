package com.api.erp.v1.features.contato.infrastructure.factory;

import com.api.erp.v1.features.contato.domain.repository.ContatoRepository;
import com.api.erp.v1.features.contato.domain.service.IContatoService;
import com.api.erp.v1.features.contato.infrastructure.decorator.*;
import com.api.erp.v1.features.contato.infrastructure.service.ContatoService;
import com.api.erp.v1.features.empresa.domain.entity.ContatoConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory para criar instâncias de ContatoService com decorators dinâmicos.
 * 
 * RESPONSABILIDADE:
 * Lê configurações da empresa e cria a chain de decorators apropriada.
 * 
 * PADRÕES UTILIZADOS:
 * - Factory Pattern: Encapsula a criação de objetos
 * - Decorator Pattern: Adiciona comportamento dinamicamente
 * - Chain of Responsibility: Decorators formam uma cadeia
 * 
 * COMPOSIÇÃO DE DECORATORS (ordem importa):
 * 1. ContatoService (base) - implementação core
 * 2. ValidationDecorator - valida dados de entrada (early fail)
 * 3. FormatValidationDecorator - valida formato (email, telefone, etc)
 * 4. CacheDecorator - cache de leituras (performance)
 * 5. AuditDecorator - registra operações (logging)
 * 6. NotificationDecorator - notifica eventos (final)
 * 
 * Esta ordem garante:
 * - Validação acontece antes de qualquer outro processamento
 * - Cache é checado antes de auditoria (evita logs falsos)
 * - Auditoria registra operações reais
 * - Notificações enviam eventos finais
 * 
 * THREAD-SAFETY:
 * Este factory é chamado apenas em:
 * 1. Inicialização (PostConstruct)
 * 2. Atualização de config (sincronizado no holder)
 * Portanto, é seguro.
 * 
 * PERFORMANCE:
 * - Factory é chamada apenas quando config muda (não em cada request)
 * - Decorators são compostos apenas 1 vez
 * - Costo é minimal comparado ao benefício
 */
@Slf4j
@RequiredArgsConstructor
public class ContatoServiceFactory {

    private final ContatoRepository repository;
    private final IEmpresaService empresaService;

    /**
     * Cria uma instância completa de ContatoService com decorators.
     * 
     * @return IContatoService pronto para uso com decorators aplicados
     */
    public IContatoService create() {
        log.info("[CONTATO FACTORY] Construindo ContatoService com decorators");
        
        ContatoConfig config = obterConfiguracao();
        IContatoService service = new ContatoService(repository);
        
        service = aplicarDecorators(service, config);
        
        log.info("[CONTATO FACTORY] ContatoService construído com sucesso");
        return service;
    }

    /**
     * Aplica decorators na ordem apropriada baseado na configuração.
     */
    private IContatoService aplicarDecorators(IContatoService service, ContatoConfig config) {
        // Ordem: ValidationDecorator é aplicado primeiro (validação early-fail)
        if (config != null && config.isContatoValidationEnabled()) {
            service = new ValidationDecoratorContatoService(service);
            log.debug("[CONTATO FACTORY] ValidationDecorator aplicado");
        }

        // Format Validation vem depois de validação básica
        if (config != null && config.isContatoFormatValidationEnabled()) {
            service = new FormatValidationDecoratorContatoService(service);
            log.debug("[CONTATO FACTORY] FormatValidationDecorator aplicado");
        }

        // Cache vem depois de validação mas antes de auditoria
        if (config != null && config.isContatoCacheEnabled()) {
            service = new CacheDecoratorContatoService(service);
            log.debug("[CONTATO FACTORY] CacheDecorator aplicado");
        }

        // Auditoria vem depois de cache
        if (config != null && config.isContatoAuditEnabled()) {
            service = new AuditDecoratorContatoService(service);
            log.debug("[CONTATO FACTORY] AuditDecorator aplicado");
        }

        // Notificação é a última na chain
        if (config != null && config.isContatoNotificationEnabled()) {
            service = new NotificationDecoratorContatoService(service);
            log.debug("[CONTATO FACTORY] NotificationDecorator aplicado");
        }

        return service;
    }

    /**
     * Obtém a configuração atual da empresa.
     * 
     * Se não houver configuração ou empresa não existir,
     * retorna null e apenas o serviço base é criado.
     */
    private ContatoConfig obterConfiguracao() {
        try {
            return empresaService.getEmpresaConfig().getContatoConfig();
        } catch (Exception e) {
            log.warn("[CONTATO FACTORY] Não foi possível obter ContatoConfig, " +
                    "usando apenas serviço base. Erro: {}", e.getMessage());
            return null;
        }
    }
}
