package com.api.erp.v1.features.cliente.infrastructure.proxy;

import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Holder para gerenciar dinamicamente a instância de ClienteValidator
 * com suporte a atualização em tempo de execução.
 * 
 * ARQUITETURA:
 * - Usa AtomicReference para armazenar a instância de forma thread-safe
 * - ReadWriteLock garante que transações não sejam interrompidas durante updates
 * - Permite recarregar decorators sem reiniciar a aplicação
 * 
 * PADRÃO SOLID:
 * - Single Responsibility: Apenas gerencia a instância do serviço
 * - Dependency Inversion: Trabalha com IClienteValidator (abstração)
 * - Open/Closed: Fechado para modificação, aberto para extensão
 * 
 * PERFORMANCE:
 * - ReadLock permite múltiplas leituras simultâneas
 * - WriteLock garante consistência na atualização
 * - Transações em andamento não são interrompidas (finish primeiro)
 * 
 * THREAD-SAFETY:
 * - AtomicReference garante visibilidade da referência em todas threads
 * - ReadWriteLock garante ordenação e consistência de memória
 */
@Slf4j
@RequiredArgsConstructor
public class ClienteValidatorHolder {

    private final AtomicReference<IClienteValidator> validatorReference;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Obtém a instância atual do serviço com lock de leitura.
     * 
     * Permite múltiplas threads lerem simultaneamente.
     * Se a configuração está sendo atualizada, espera o WriteLock ser liberado.
     */
    public IClienteValidator getValidator() {
        lock.readLock().lock();
        try {
            IClienteValidator validator = validatorReference.get();
            if (validator == null) {
                throw new IllegalStateException("ClienteValidator não foi inicializado");
            }
            return validator;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Atualiza a instância do serviço com lock de escrita.
     * 
     * Garante que nenhuma leitura ocorre durante a atualização.
     * Usa AtomicReference.set() para garantir visibilidade em todas threads.
     */
    public void updateValidator(IClienteValidator newValidator) {
        lock.writeLock().lock();
        try {
            IClienteValidator oldValidator = validatorReference.getAndSet(newValidator);
            log.info("[CLIENTE/Validator HOLDER] Serviço atualizado. " +
                    "Classe anterior: {}, Nova classe: {}",
                    oldValidator != null ? oldValidator.getClass().getSimpleName() : "null",
                    newValidator.getClass().getSimpleName());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retorna a instância atual sem lock (apenas para debug).
     * NÃO use isso em operações críticas.
     */
    public IClienteValidator getPeekValidator() {
        return validatorReference.get();
    }
}
