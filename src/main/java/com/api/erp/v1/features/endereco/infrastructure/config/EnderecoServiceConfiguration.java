package com.api.erp.v1.features.endereco.infrastructure.config;

import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.v1.features.endereco.domain.service.IEnderecoService;
import com.api.erp.v1.features.endereco.infrastructure.event.EnderecoConfigUpdateListener;
import com.api.erp.v1.features.endereco.infrastructure.factory.EnderecoServiceFactory;
import com.api.erp.v1.features.endereco.infrastructure.proxy.EnderecoServiceHolder;
import com.api.erp.v1.features.endereco.infrastructure.proxy.EnderecoServiceProxy;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Configuração Spring para Endereco Service com Decorators Dinâmicos
 * 
 * ARQUITETURA:
 * 1. EnderecoServiceFactory - cria instâncias com decorators
 * 2. EnderecoServiceHolder - gerencia referência com ReadWriteLock
 * 3. EnderecoServiceProxy - implementa IEnderecoService e delega para holder
 * 4. EnderecoConfigUpdateListener - escuta eventos e recarrega decorators
 * 
 * FLUXO:
 * Application Start:
 *   → Cria Factory, Holder, Proxy
 *   → Proxy chama factory.create() no init
 *   → Holder armazena instância com decorators
 * 
 * Request:
 *   → Controller injeta Proxy (que é Primary para IEnderecoService)
 *   → Proxy.metodo() chama holder.getService()
 *   → Holder retorna instância atual (com ReadLock)
 *   → Operação executa normalmente
 * 
 * Config Update:
 *   → EmpresaService.updateEnderecoConfig()
 *   → Publica EnderecoConfigUpdateEvent
 *   → EnderecoConfigUpdateListener escuta (async)
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
 * @see EnderecoServiceFactory
 * @see EnderecoServiceHolder
 * @see EnderecoServiceProxy
 * @see EnderecoConfigUpdateListener
 */
@Slf4j
@Configuration
public class EnderecoServiceConfiguration {

    /**
     * Cria e registra o factory responsável por criar
     * instâncias de EnderecoService com decorators.
     */
    @Bean
    public EnderecoServiceFactory enderecoServiceFactory(
            EnderecoRepository repository,
            IEmpresaService empresaService) {
        
        log.info("[ENDERECO CONFIG] Inicializando EnderecoServiceFactory");
        return new EnderecoServiceFactory(repository, empresaService);
    }

    /**
     * Cria o holder que gerencia a instância com suporte
     * a atualização dinâmica.
     * 
     * AtomicReference garante que:
     * - Mudanças na referência são visíveis em todas threads
     * - Não há race conditions na atribuição
     * 
     * Inicializa com serviço vazio - será preenchido no EnderecoServiceProxy.init()
     */
    @Bean
    public EnderecoServiceHolder enderecoServiceHolder() {
        log.info("[ENDERECO CONFIG] Inicializando EnderecoServiceHolder");
        return new EnderecoServiceHolder(new AtomicReference<>());
    }

    /**
     * Cria o proxy que implementa IEnderecoService e é injetado
     * em componentes que precisam do serviço.
     * 
     * Este é o bean PRIMARY - quando um componente injeta IEnderecoService,
     * esta é a implementação que recebe.
     * 
     * O proxy sempre delega para holder.getService(), permitindo
     * que a instância seja trocada dinamicamente.
     */
    @Bean
    @Primary
    public IEnderecoService enderecoService(EnderecoServiceHolder holder) {
        log.info("[ENDERECO CONFIG] Inicializando EnderecoServiceProxy");
        return new EnderecoServiceProxy(holder);
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
    public ProxyInitializer enderecoServiceProxyInitializer(
            EnderecoServiceFactory factory,
            EnderecoServiceHolder holder) {
        
        return new ProxyInitializer(factory, holder);
    }

    /**
     * Classe interna para inicializar o proxy após criação dos beans.
     * 
     * Necessária pois beans precisam ser criados antes de poder inicializar.
     */
    @Slf4j
    public static class ProxyInitializer {
        public ProxyInitializer(EnderecoServiceFactory factory, EnderecoServiceHolder holder) {
            log.info("[ENDERECO CONFIG] Inicializando EnderecoServiceProxy com decorators iniciais");
            var initialService = factory.create();
            holder.updateService(initialService);
            log.info("[ENDERECO CONFIG] EnderecoServiceProxy inicializado com sucesso");
        }
    }
}
