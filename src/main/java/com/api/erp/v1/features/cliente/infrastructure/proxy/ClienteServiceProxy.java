package com.api.erp.v1.features.cliente.infrastructure.proxy;

import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Proxy de ClienteService que delega para ClienteServiceHolder.
 * 
 * Este proxy é injetado como bean primary de IClienteService.
 * Ele sempre delega para o serviço atual no holder, permitindo
 * que o serviço seja trocado dinamicamente sem reiniciar a aplicação.
 * 
 * FUNCIONAMENTO:
 * 1. Componentes injetam ClienteServiceProxy (que é um IClienteService)
 * 2. Proxy chama holder.getService() para obter a instância atual
 * 3. Delega a chamada para o serviço com decorators atualizados
 * 4. Se configurações mudam, holder.updateService() é chamado
 * 5. Próximas chamadas já usam os novos decorators
 * 
 * THREAD-SAFE: Sim, o holder gerencia a sincronização
 * 
 * TRANSAÇÕES: Seguro, pois obtém o serviço antes da transação
 * iniciar e o mantém durante toda operação
 */
@RequiredArgsConstructor
public class ClienteServiceProxy implements IClienteService {

    private final ClienteServiceHolder holder;

    @Override
    public Page<Cliente> pegarTodos(Pageable pageable) {
        return holder.getService().pegarTodos(pageable);
    }

    @Override
    public Cliente criar(CreateClienteDto clienteDto) {
        return holder.getService().criar(clienteDto);
    }

    @Override
    public Cliente atualizar(Long id, CreateClienteDto clienteDto) {
        return holder.getService().atualizar(id, clienteDto);
    }

    @Override
    public Cliente pegarPorId(Long id) {
        return holder.getService().pegarPorId(id);
    }

    @Override
    public void deletar(Long id) {
        holder.getService().deletar(id);
    }
}
