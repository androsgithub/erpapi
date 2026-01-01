package com.api.erp.v1.features.contato.infrastructure.proxy;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.features.contato.domain.service.IContatoService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Proxy de ContatoService que delega para ContatoServiceHolder.
 * 
 * Este proxy é injetado como bean primary de IContatoService.
 * Ele sempre delega para o serviço atual no holder, permitindo
 * que o serviço seja trocado dinamicamente sem reiniciar a aplicação.
 * 
 * FUNCIONAMENTO:
 * 1. Componentes injetam ContatoServiceProxy (que é um IContatoService)
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
public class ContatoServiceProxy implements IContatoService {

    private final ContatoServiceHolder holder;

    @Override
    public Contato criar(CreateContatoRequest request) {
        return holder.getService().criar(request);
    }

    @Override
    public Contato buscarPorId(Long id) {
        return holder.getService().buscarPorId(id);
    }

    @Override
    public List<Contato> buscarTodos() {
        return holder.getService().buscarTodos();
    }

    @Override
    public List<Contato> buscarAtivos() {
        return holder.getService().buscarAtivos();
    }

    @Override
    public List<Contato> buscarInativos() {
        return holder.getService().buscarInativos();
    }

    @Override
    public List<Contato> buscarPorTipo(String tipo) {
        return holder.getService().buscarPorTipo(tipo);
    }

    @Override
    public Contato buscarPrincipal() {
        return holder.getService().buscarPrincipal();
    }

    @Override
    public Contato atualizar(Long id, CreateContatoRequest request) {
        return holder.getService().atualizar(id, request);
    }

    @Override
    public Contato ativar(Long id) {
        return holder.getService().ativar(id);
    }

    @Override
    public Contato desativar(Long id) {
        return holder.getService().desativar(id);
    }

    @Override
    public void deletar(Long id) {
        holder.getService().deletar(id);
    }
}
