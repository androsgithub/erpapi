package com.api.erp.v1.features.cliente.infrastructure.proxy;

import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Slf4j
@RequiredArgsConstructor
public class ClienteServiceHolder {

    private final AtomicReference<IClienteService> serviceReference;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public IClienteService getService() {
        lock.readLock().lock();
        try {
            IClienteService service = serviceReference.get();
            if (service == null) {
                throw new IllegalStateException("ClienteService não foi inicializado");
            }
            return service;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateService(IClienteService newService) {
        lock.writeLock().lock();
        try {
            IClienteService oldService = serviceReference.getAndSet(newService);
            log.info("[CLIENTE SERVICE HOLDER] Serviço atualizado. " + "Classe anterior: {}, Nova classe: {}", oldService != null ? oldService.getClass().getSimpleName() : "null", newService.getClass().getSimpleName());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public IClienteService getPeekService() {
        return serviceReference.get();
    }
}
