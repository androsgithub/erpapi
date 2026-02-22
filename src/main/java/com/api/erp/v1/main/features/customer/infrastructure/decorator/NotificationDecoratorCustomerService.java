package com.api.erp.v1.main.features.customer.infrastructure.decorator;

import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import com.api.erp.v1.main.features.customer.domain.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@RequiredArgsConstructor
public class NotificationDecoratorCustomerService implements ICustomerService {

    private final ICustomerService service;

    @Override
    public Page<Customer> pegarTodos(Pageable pageable) {
        return service.pegarTodos(pageable);
    }

    @Override
    public Customer criar(CreateCustomerDto customerDto) {
        Customer customer = service.criar(customerDto);
        notificarCustomerCriado(customer);
        return customer;
    }

    @Override
    public Customer atualizar(Long id, CreateCustomerDto customerDto) {
        Customer customer = service.atualizar(id, customerDto);
        notificarCustomerAtualizado(customer);
        return customer;
    }

    @Override
    public Customer pegarPorId(Long id) {
        return service.pegarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        Customer customer = service.pegarPorId(id);
        service.deletar(id);
        notificarCustomerDeletado(customer);
    }

    private void notificarCustomerCriado(Customer customer) {
        log.info("[NOTIFICATION CUSTOMER] Evento: Customer criado - id={}, nome={}, cnpj={}",
                customer.getId(),
                customer.getNome(),
                customer.getDadosFiscais().getCnpj());
    }

    private void notificarCustomerAtualizado(Customer customer) {
        log.info("[NOTIFICATION CUSTOMER] Evento: Customer atualizado - id={}, nome={}",
                customer.getId(), customer.getNome());
    }

    private void notificarCustomerDeletado(Customer customer) {
        log.warn("[NOTIFICATION CUSTOMER] Evento: Customer deletado - id={}, nome={}, cnpj={}",
                customer.getId(),
                customer.getNome(),
                customer.getDadosFiscais().getCnpj());
    }
}
