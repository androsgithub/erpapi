package com.api.erp.v1.features.cliente.infrastructure.config;

import com.api.erp.v1.features.cliente.domain.repository.ClienteRepository;
import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.features.cliente.infrastructure.event.ClienteConfigUpdateListener;
import com.api.erp.v1.features.cliente.infrastructure.factory.ClienteServiceFactory;
import com.api.erp.v1.features.cliente.infrastructure.proxy.ClienteServiceHolder;
import com.api.erp.v1.features.cliente.infrastructure.proxy.ClienteServiceProxy;
import com.api.erp.v1.features.empresa.domain.service.IEmpresaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Configuração Spring para Cliente Service com Decorators Dinâmicos
 * 
 * ARQUITETURA:
 * 1. ClienteServiceFactory - cria instâncias com decorators
 * 2. ClienteServiceHolder - gerencia referência com ReadWriteLock
 * 3. ClienteServiceProxy - implementa IClienteService e delega para holder
 * 4. ClienteConfigUpdateListener - escuta eventos e recarrega decorators
 * 
 * FLUXO:
 * Application Start:
 *   → Cria Factory, Holder, Proxy
 *   → Proxy chama factory.create() no init
 *   → Holder armazena instância com decorators
 * 
 * Request:
 *   → Controller injeta Proxy (que é Primary para IClienteService)
 *   → Proxy.metodo() chama holder.getService()
 *   → Holder retorna instância atual (com ReadLock)
 *   → Operação executa normalmente
 * 
 * Config Update:
 *   → EmpresaService.updateClienteConfig()
 *   → Publica ClienteConfigUpdateEvent
 *   → ClienteConfigUpdateListener escuta (async)
 *   → Reconstrói factory.create()
 *   → holder.updateService() com WriteLock
 *   → Próximo request já usa nova config
 * 
 * PADRÕES SOLID:
 * 
 * Single Responsibility:
 * - Factory: cria com decorators
 * - Holder: gerencia referência
 * - Proxy: implementa interface e delega
 * - Listener: recarrega em eventos
 * 
 * Open/Closed:
 * - Decorators podem ser adicionados sem modificar service
 * - Novos decorators = novo decorator class + config flag
 * 
 * Liskov Substitution:
 * - Proxy substitui IClienteService sem quebrar contrato
 * - Decorators substituem IClienteService transparente
 * 
 * Interface Segregation:
 * - IClienteService define apenas métodos necessários
 * - Cada decorator implementa interface completa
 * 
 * Dependency Inversion:
 * - Componentes dependem de IClienteService (abstração)
 * - Não dependem de implementação concreta
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
 * @see ClienteServiceFactory
 * @see ClienteServiceHolder
 * @see ClienteServiceProxy
 * @see ClienteConfigUpdateListener
 */
@Slf4j
@Configuration
public class ClienteServiceConfiguration {

    /**
     * Cria e registra o factory responsável por criar
     * instâncias de ClienteService com decorators.
     */
    @Bean
    public ClienteServiceFactory clienteServiceFactory(
            ClienteRepository repository,
            IClienteValidator validator,
            IEmpresaService empresaService) {
        
        log.info("[CLIENTE CONFIG] Inicializando ClienteServiceFactory");
        return new ClienteServiceFactory(repository, validator, empresaService);
    }

    /**
     * Cria o holder que gerencia a instância com suporte
     * a atualização dinâmica.
     * 
     * AtomicReference garante que:
     * - Mudanças na referência são visíveis em todas threads
     * - Não há race conditions na atribuição
     * 
     * Inicializa com serviço vazio - será preenchido no ClienteServiceProxy.init()
     */
    @Bean
    public ClienteServiceHolder clienteServiceHolder() {
        log.info("[CLIENTE CONFIG] Inicializando ClienteServiceHolder");
        return new ClienteServiceHolder(new AtomicReference<>());
    }

    /**
     * Cria o proxy que implementa IClienteService e é injetado
     * em componentes que precisam do serviço.
     * 
     * Este é o bean PRIMARY - quando um componente injeta IClienteService,
     * esta é a implementação que recebe.
     * 
     * O proxy sempre delega para holder.getService(), permitindo
     * que a instância seja trocada dinamicamente.
     */
    @Bean
    @Primary
    public IClienteService clienteService(ClienteServiceHolder holder) {
        log.info("[CLIENTE CONFIG] Inicializando ClienteServiceProxy");
        return new ClienteServiceProxy(holder);
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
    public ProxyInitializer clienteServiceProxyInitializer(
            ClienteServiceFactory factory,
            ClienteServiceHolder holder) {
        
        return new ProxyInitializer(factory, holder);
    }

    /**
     * Classe interna para inicializar o proxy após criação dos beans.
     * 
     * Necessária pois beans precisam ser criados antes de poder inicializar.
     */
    @Slf4j
    public static class ProxyInitializer {
        public ProxyInitializer(ClienteServiceFactory factory, ClienteServiceHolder holder) {
            log.info("[CLIENTE CONFIG] Inicializando ClienteServiceProxy com decorators iniciais");
            var initialService = factory.create();
            holder.updateService(initialService);
            log.info("[CLIENTE CONFIG] ClienteServiceProxy inicializado com sucesso");
        }
    }
}
