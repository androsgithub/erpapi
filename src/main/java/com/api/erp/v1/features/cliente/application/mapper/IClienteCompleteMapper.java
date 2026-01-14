package com.api.erp.v1.features.cliente.application.mapper;

import com.api.erp.v1.features.cliente.application.dto.response.ClienteCompleteResponseDto;
import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.features.contato.application.dto.response.ContatoResponse;
import com.api.erp.v1.features.contato.domain.entity.ClienteContato;
import com.api.erp.v1.features.endereco.application.dto.response.EnderecoResponse;
import com.api.erp.v1.features.endereco.domain.entity.ClienteEndereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ClienteDadosFiscaisMapper.class)
public interface IClienteCompleteMapper {

    @Mapping(target = "dadosFiscais", source = "dadosFiscais")
    @Mapping(target = "contatos", expression = "java(mapearContatos(cliente.getContatos()))")
    @Mapping(target = "enderecos", expression = "java(mapearEnderecos(cliente.getEnderecos()))")
    ClienteCompleteResponseDto toResponse(Cliente cliente);

    List<ClienteCompleteResponseDto> toResponseList(List<Cliente> clientes);

    default Set<ContatoResponse> mapearContatos(Set<ClienteContato> clienteContatos) {
        if (clienteContatos == null || clienteContatos.isEmpty()) {
            return Set.of();
        }

        return clienteContatos
                .stream()
                .filter(uc -> uc.getContato() != null)
                .map(uc -> new ContatoResponse(uc.getContato().getId(),
                        uc.getContato().getTipo() != null ? uc.getContato().getTipo().toString() : null,
                        uc.getContato().getValor(),
                        uc.getContato().getDescricao(),
                        uc.getContato().isPrincipal(),
                        uc.getContato().isAtivo(),
                        uc.getCliente().getCustomData(),
                        uc.getContato().getCreatedAt().toLocalDateTime(),
                        uc.getContato().getUpdatedAt().toLocalDateTime())).collect(Collectors.toSet());
    }

    default Set<EnderecoResponse> mapearEnderecos(Set<ClienteEndereco> clienteEnderecos) {
        if (clienteEnderecos == null || clienteEnderecos.isEmpty()) {
            return Set.of();
        }

        return clienteEnderecos
                .stream()
                .filter(uc -> uc.getEndereco() != null)
                .map(uc -> new EnderecoResponse(
                        uc.getEndereco().getId(),
                        uc.getEndereco().getRua().toString(),
                        uc.getEndereco().getNumero(),
                        uc.getEndereco().getComplemento(),
                        uc.getEndereco().getBairro(),
                        uc.getEndereco().getCidade(),
                        uc.getEndereco().getEstado(),
                        uc.getEndereco().getCep(),
                        uc.getEndereco().getTipo(),
                        uc.getEndereco().getPrincipal(),
                        uc.getEndereco().getCustomData(),
                        uc.getEndereco().getCreatedAt(),
                        uc.getEndereco().getUpdatedAt(),
                        uc.getEndereco().getCreatedBy(),
                        uc.getEndereco().getUpdatedBy())).collect(Collectors.toSet());
    }

    default Page<ClienteCompleteResponseDto> toResponsePage(Page<Cliente> page) {
        return page.map(this::toResponse);
    }
}
