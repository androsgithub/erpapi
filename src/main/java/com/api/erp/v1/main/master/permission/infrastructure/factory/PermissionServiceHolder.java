package com.api.erp.v1.main.master.permission.infrastructure.factory;

import com.api.erp.v1.main.master.permission.domain.service.IPermissionService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Holder para gerenciar dinamicamente a instância de PermissionService
 * com suporte a atualização em tempo de execução.
 */
@Slf4j
public class PermissionServiceHolder {

    private final AtomicReference<IPermissionService> serviceReference;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public PermissionServiceHolder() {
        this.serviceReference = new AtomicReference<>();
    }

    public IPermissionService getService() {
        lock.readLock().lock();
        try {
            IPermissionService service = serviceReference.get();
            if (service == null) {
                throw new IllegalStateException("PermissionService was not initialized");
            }
            return service;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateService(IPermissionService newService) {
        lock.writeLock().lock();
        try {
            IPermissionService oldService = serviceReference.getAndSet(newService);
            log.info("[PERMISSION SERVICE HOLDER] Service updated. "
 +
                    "Classe anterior: {}, Nova classe: {}",
                    oldService != null ? oldService.getClass().getSimpleName() : "null",
                    newService.getClass().getSimpleName());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public IPermissionService getPeekService() {
        return serviceReference.get();
    }
}
