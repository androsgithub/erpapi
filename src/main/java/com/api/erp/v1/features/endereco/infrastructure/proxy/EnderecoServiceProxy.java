package com.api.erp.v1.features.endereco.infrastructure.proxy;

import com.api.erp.v1.features.endereco.application.dto.CreateEnderecoRequest;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.service.IEnderecoService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Proxy de EnderecoService que delega para EnderecoServiceHolder.
 * 
 * Este proxy é injetado como bean primary de IEnderecoService.
 * Ele sempre delega para o serviço atual no holder, permitindo
 * que o serviço seja trocado dinamicamente sem reiniciar a aplicação.
 * 
 * FUNCIONAMENTO:
 * 1. Componentes injetam EnderecoServiceProxy (que é um IEnderecoService)
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
public class EnderecoServiceProxy implements IEnderecoService {

    private final EnderecoServiceHolder holder;

    @Override
    public Endereco criar(CreateEnderecoRequest request) {
        return holder.getService().criar(request);
    }

    @Override
    public Endereco buscarPorId(Long id) {
        return holder.getService().buscarPorId(id);
    }

    @Override
    public List<Endereco> buscarTodos() {
        return holder.getService().buscarTodos();
    }

    @Override
    public Endereco atualizar(Long id, CreateEnderecoRequest request) {
        return holder.getService().atualizar(id, request);
    }

    @Override
    public void deletar(Long id) {
        holder.getService().deletar(id);
    }
}
