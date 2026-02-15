package com.api.erp.v1.main.features.permissao.infrastructure.factory;

import com.api.erp.v1.main.features.permissao.domain.service.IPermissaoService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Holder para gerenciar dinamicamente a instância de PermissaoService
 * com suporte a atualização em tempo de execução.
 */
@Slf4j
public class PermissaoServiceHolder {

    private final AtomicReference<IPermissaoService> serviceReference;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public PermissaoServiceHolder() {
        this.serviceReference = new AtomicReference<>();
    }

    public IPermissaoService getService() {
        lock.readLock().lock();
        try {
            IPermissaoService service = serviceReference.get();
            if (service == null) {
                throw new IllegalStateException("PermissaoService não foi inicializado");
            }
            return service;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateService(IPermissaoService newService) {
        lock.writeLock().lock();
        try {
            IPermissaoService oldService = serviceReference.getAndSet(newService);
            log.info("[PERMISSAO SERVICE HOLDER] Serviço atualizado. " +
                    "Classe anterior: {}, Nova classe: {}",
                    oldService != null ? oldService.getClass().getSimpleName() : "null",
                    newService.getClass().getSimpleName());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public IPermissaoService getPeekService() {
        return serviceReference.get();
    }
}
