package com.api.erp.v1.features.contato.infrastructure.config;

import com.api.erp.v1.features.contato.domain.repository.ContatoRepository;
import com.api.erp.v1.features.contato.domain.service.IContatoService;
import com.api.erp.v1.features.contato.infrastructure.event.ContatoConfigUpdateListener;
import com.api.erp.v1.features.contato.infrastructure.factory.ContatoServiceFactory;
import com.api.erp.v1.features.contato.infrastructure.proxy.ContatoServiceHolder;
import com.api.erp.v1.features.contato.infrastructure.proxy.ContatoServiceProxy;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Configuração Spring para Contato Service com Decorators Dinâmicos
 * 
 * ARQUITETURA:
 * 1. ContatoServiceFactory - cria instâncias com decorators
 * 2. ContatoServiceHolder - gerencia referência com ReadWriteLock
 * 3. ContatoServiceProxy - implementa IContatoService e delega para holder
 * 4. ContatoConfigUpdateListener - escuta eventos e recarrega decorators
 * 
 * FLUXO:
 * Application Start:
 *   → Cria Factory, Holder, Proxy
 *   → Proxy chama factory.create() no init
 *   → Holder armazena instância com decorators
 * 
 * Request:
 *   → Controller injeta Proxy (que é Primary para IContatoService)
 *   → Proxy.metodo() chama holder.getService()
 *   → Holder retorna instância atual (com ReadLock)
 *   → Operação executa normalmente
 * 
 * Config Update:
 *   → EmpresaService.updateContatoConfig()
 *   → Publica ContatoConfigUpdateEvent
 *   → ContatoConfigUpdateListener escuta (async)
 *   → Reconstrói factory.create()
 *   → holder.updateService() com WriteLock
 *   → Próximo request já usa nova config
 * 
 * PADRÕES SOLID: Mesmo que ClienteServiceConfiguration
 * 
 * THREAD-SAFETY:
 * - AtomicReference garante visibilidade
 * - ReadWriteLock garante sincronização
 * - Transações não são interrompidas
 * 
 * PERFORMANCE:
 * - ReadLock permite múltiplas leituras
 * - WriteLock só usado em atualização de config (raro)
 * - Cache decorator opcional para melhor performance
 * - Sem impacto na performance geral do sistema
 * 
 * @see ContatoServiceFactory
 * @see ContatoServiceHolder
 * @see ContatoServiceProxy
 * @see ContatoConfigUpdateListener
 */
@Slf4j
@Configuration
public class ContatoServiceConfiguration {

    /**
     * Cria e registra o factory responsável por criar
     * instâncias de ContatoService com decorators.
     */
    @Bean
    public ContatoServiceFactory contatoServiceFactory(
            ContatoRepository repository,
            IEmpresaService empresaService) {
        
        log.info("[CONTATO CONFIG] Inicializando ContatoServiceFactory");
        return new ContatoServiceFactory(repository, empresaService);
    }

    /**
     * Cria o holder que gerencia a instância com suporte
     * a atualização dinâmica.
     * 
     * AtomicReference garante que:
     * - Mudanças na referência são visíveis em todas threads
     * - Não há race conditions na atribuição
     * 
     * Inicializa com serviço vazio - será preenchido no ContatoServiceProxy.init()
     */
    @Bean
    public ContatoServiceHolder contatoServiceHolder() {
        log.info("[CONTATO CONFIG] Inicializando ContatoServiceHolder");
        return new ContatoServiceHolder(new AtomicReference<>());
    }

    /**
     * Cria o proxy que implementa IContatoService e é injetado
     * em componentes que precisam do serviço.
     * 
     * Este é o bean PRIMARY - quando um componente injeta IContatoService,
     * esta é a implementação que recebe.
     * 
     * O proxy sempre delega para holder.getService(), permitindo
     * que a instância seja trocada dinamicamente.
     */
    @Bean
    @Primary
    public IContatoService contatoService(ContatoServiceHolder holder) {
        log.info("[CONTATO CONFIG] Inicializando ContatoServiceProxy");
        return new ContatoServiceProxy(holder);
    }

    /**
     * Post-construct do proxy para inicializar o holder com a instância inicial.
     * 
     * Este bean é executado após o proxy ser criado e:
     * 1. Obtém a factory
     * 2. Chama factory.create() para criar instância com decorators
     * 3. Atualiza holder com a instância
     * 4. Sistema está pronto para receber requisições
     */
    @Bean
    public ProxyInitializer contatoServiceProxyInitializer(
            ContatoServiceFactory factory,
            ContatoServiceHolder holder) {
        
        return new ProxyInitializer(factory, holder);
    }

    /**
     * Classe interna para inicializar o proxy após criação dos beans.
     * 
     * Necessária pois beans precisam ser criados antes de poder inicializar.
     */
    @Slf4j
    public static class ProxyInitializer {
        public ProxyInitializer(ContatoServiceFactory factory, ContatoServiceHolder holder) {
            log.info("[CONTATO CONFIG] Inicializando ContatoServiceProxy com decorators iniciais");
            var initialService = factory.create();
            holder.updateService(initialService);
            log.info("[CONTATO CONFIG] ContatoServiceProxy inicializado com sucesso");
        }
    }
}
