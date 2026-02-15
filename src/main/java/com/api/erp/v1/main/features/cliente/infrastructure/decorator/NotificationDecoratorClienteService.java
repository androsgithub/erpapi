package com.api.erp.v1.main.features.cliente.infrastructure.decorator;

import com.api.erp.v1.main.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.main.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.main.features.cliente.domain.service.IClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@RequiredArgsConstructor
public class NotificationDecoratorClienteService implements IClienteService {

    private final IClienteService service;

    @Override
    public Page<Cliente> pegarTodos(Pageable pageable) {
        return service.pegarTodos(pageable);
    }

    @Override
    public Cliente criar(CreateClienteDto clienteDto) {
        Cliente cliente = service.criar(clienteDto);
        notificarClienteCriado(cliente);
        return cliente;
    }

    @Override
    public Cliente atualizar(Long id, CreateClienteDto clienteDto) {
        Cliente cliente = service.atualizar(id, clienteDto);
        notificarClienteAtualizado(cliente);
        return cliente;
    }

    @Override
    public Cliente pegarPorId(Long id) {
        return service.pegarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        Cliente cliente = service.pegarPorId(id);
        service.deletar(id);
        notificarClienteDeletado(cliente);
    }

    private void notificarClienteCriado(Cliente cliente) {
        log.info("[NOTIFICATION CLIENTE] Evento: Cliente criado - id={}, nome={}, cnpj={}",
                cliente.getId(),
                cliente.getNome(),
                cliente.getDadosFiscais().getCnpj());
    }

    private void notificarClienteAtualizado(Cliente cliente) {
        log.info("[NOTIFICATION CLIENTE] Evento: Cliente atualizado - id={}, nome={}",
                cliente.getId(), cliente.getNome());
    }

    private void notificarClienteDeletado(Cliente cliente) {
        log.warn("[NOTIFICATION CLIENTE] Evento: Cliente deletado - id={}, nome={}, cnpj={}",
                cliente.getId(),
                cliente.getNome(),
                cliente.getDadosFiscais().getCnpj());
    }
}
