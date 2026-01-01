package com.api.erp.v1.features.endereco.infrastructure.factory;

import com.api.erp.v1.features.empresa.domain.entity.EnderecoConfig;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.v1.features.endereco.domain.service.IEnderecoService;
import com.api.erp.v1.features.endereco.infrastructure.decorator.AuditDecoratorEnderecoService;
import com.api.erp.v1.features.endereco.infrastructure.decorator.CacheDecoratorEnderecoService;
import com.api.erp.v1.features.endereco.infrastructure.decorator.ValidationDecoratorEnderecoService;
import com.api.erp.v1.features.endereco.infrastructure.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory para criar instâncias de EnderecoService com decorators dinâmicos.
 * <p>
 * RESPONSABILIDADE:
 * Lê configurações da empresa e cria a chain de decorators apropriada.
 * <p>
 * PADRÕES UTILIZADOS:
 * - Factory Pattern: Encapsula a criação de objetos
 * - Decorator Pattern: Adiciona comportamento dinamicamente
 * - Chain of Responsibility: Decorators formam uma cadeia
 * <p>
 * COMPOSIÇÃO DE DECORATORS (ordem importa):
 * 1. EnderecoService (base) - implementação core
 * 2. ValidationDecorator - valida dados de entrada (early fail)
 * 3. CacheDecorator - cache de leituras (performance)
 * 4. AuditDecorator - registra operações (logging)
 * <p>
 * Esta ordem garante:
 * - Validação acontece antes de qualquer outro processamento
 * - Cache é checado antes de auditoria (evita logs falsos)
 * - Auditoria registra operações reais
 * <p>
 * THREAD-SAFETY:
 * Este factory é chamado apenas em:
 * 1. Inicialização (PostConstruct)
 * 2. Atualização de config (sincronizado no holder)
 * Portanto, é seguro.
 * <p>
 * PERFORMANCE:
 * - Factory é chamada apenas quando config muda (não em cada request)
 * - Decorators são compostos apenas 1 vez
 * - Costo é minimal comparado ao benefício
 */
@Slf4j
@RequiredArgsConstructor
public class EnderecoServiceFactory {

    private final EnderecoRepository repository;
    private final IEmpresaService empresaService;

    /**
     * Cria uma instância completa de EnderecoService com decorators.
     *
     * @return IEnderecoService pronto para uso com decorators aplicados
     */
    public IEnderecoService create() {
        log.info("[ENDERECO FACTORY] Construindo EnderecoService com decorators");

        EnderecoConfig config = obterConfiguracao();
        IEnderecoService service = new EnderecoService(repository);

        service = aplicarDecorators(service, config);

        log.info("[ENDERECO FACTORY] EnderecoService construído com sucesso");
        return service;
    }

    /**
     * Aplica decorators na ordem apropriada baseado na configuração.
     */
    private IEnderecoService aplicarDecorators(IEnderecoService service, EnderecoConfig config) {
        // Ordem: ValidationDecorator é aplicado primeiro (validação early-fail)
        if (config != null && config.isEnderecoValidationEnabled()) {
            log.debug("[ENDERECO FACTORY] ValidationDecorator aplicado");
            service = new ValidationDecoratorEnderecoService(service);
        }

        // Cache é aplicado antes de auditoria
        if (config != null && config.isEnderecoCacheEnabled()) {
            log.debug("[ENDERECO FACTORY] CacheDecorator aplicado");
            service = new CacheDecoratorEnderecoService(service);
        }

        // Auditoria é a penúltima na chain
        if (config != null && config.isEnderecoAuditEnabled()) {
            log.debug("[ENDERECO FACTORY] AuditDecorator aplicado");
            service = new AuditDecoratorEnderecoService(service);
        }

        return service;
    }

    /**
     * Obtém a configuração atual da empresa.
     * <p>
     * Se não houver configuração ou empresa não existir,
     * retorna null e apenas o serviço base é criado.
     */
    private EnderecoConfig obterConfiguracao() {
        try {
            return empresaService.getEmpresaConfig().getEnderecoConfig();
        } catch (Exception e) {
            log.warn("[ENDERECO FACTORY] Não foi possível obter EnderecoConfig, " +
                    "usando apenas serviço base. Erro: {}", e.getMessage());
            return null;
        }
    }
}

