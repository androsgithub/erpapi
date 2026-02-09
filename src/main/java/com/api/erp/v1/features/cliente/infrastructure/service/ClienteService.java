package com.api.erp.v1.features.cliente.infrastructure.service;

import com.api.erp.v1.features.cliente.application.dto.ClienteDadosFinanceirosDto;
import com.api.erp.v1.features.cliente.application.dto.ClienteDadosFiscaisDto;
import com.api.erp.v1.features.cliente.application.dto.ClientePreferenciasDto;
import com.api.erp.v1.features.cliente.application.dto.request.CreateClienteDto;
import com.api.erp.v1.features.cliente.domain.entity.Cliente;
import com.api.erp.v1.features.cliente.domain.entity.ClienteDadosFinanceiros;
import com.api.erp.v1.features.cliente.domain.entity.ClienteDadosFiscais;
import com.api.erp.v1.features.cliente.domain.entity.ClientePreferencias;
import com.api.erp.v1.features.cliente.domain.repository.ClienteRepository;
import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.shared.domain.exception.NotFoundException;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.CPF;
import com.api.erp.v1.shared.domain.valueobject.RG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements IClienteService {

    private final ClienteRepository repository;
    @Qualifier("clienteValidatorProxy")
    private final IClienteValidator validator;

    @Autowired
    public ClienteService(ClienteRepository repository, IClienteValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public Page<Cliente> pegarTodos(Pageable pageable) {
        validator.validarPageable(pageable);
        return repository.findAll(pageable);
    }

    @Override
    public Cliente pegarPorId(Long id) {
        validator.validarId(id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Cliente não encontrado com id: " + id));
    }

    @Override
    public Cliente criar(CreateClienteDto clienteDto) {
        validator.validarCriacao(clienteDto);
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDto.nome());
        cliente.setStatus(clienteDto.status());
        cliente.setTipo(clienteDto.tipoCliente());
        ClienteDadosFiscaisDto dadosFiscaisDto = clienteDto.dadosFiscais();
        ClienteDadosFiscais dadosFiscais = new ClienteDadosFiscais(
                dadosFiscaisDto.razaoSocial(),
                dadosFiscaisDto.nomeFantasia(),
                dadosFiscaisDto.cnpj() == null ? null : new CNPJ(dadosFiscaisDto.cnpj()),
                dadosFiscaisDto.cpf() == null ? null : new CPF(dadosFiscaisDto.cpf()),
                dadosFiscaisDto.rg() == null ? null : new RG(dadosFiscaisDto.rg()),
                dadosFiscaisDto.inscricaoEstadual(),
                dadosFiscaisDto.inscricaoMunicipal(),
                dadosFiscaisDto.regimeTributario(),
                dadosFiscaisDto.icmsContribuinte(),
                dadosFiscaisDto.aliquotaIcms(),
                dadosFiscaisDto.cnaePrincipal(),
                dadosFiscaisDto.consumidorFinal()
        );
        cliente.setDadosFiscais(dadosFiscais);

        ClienteDadosFinanceirosDto dadosFinanceirosDto = clienteDto.dadosFinanceiros();
        ClienteDadosFinanceiros dadosFinanceiros = new ClienteDadosFinanceiros(
                dadosFinanceirosDto.limiteCredito(),
                dadosFinanceirosDto.limiteDesconto(),
                dadosFinanceirosDto.restricaoFinanceira(),
                dadosFinanceirosDto.protestado()
        );
        cliente.setDadosFinanceiros(dadosFinanceiros);

        ClientePreferenciasDto preferenciasDto = clienteDto.preferencias();
        ClientePreferencias preferencias = new ClientePreferencias(
                preferenciasDto.emailPrincipal(),
                preferenciasDto.emailNfe(),
                preferenciasDto.enviarEmail(),
                preferenciasDto.malaDireta()
        );
        cliente.setPreferencias(preferencias);

        return repository.save(cliente);
    }

    @Override
    public Cliente atualizar(Long id, CreateClienteDto clienteDto) {
        validator.validarAtualizacao(id, clienteDto);
        Cliente cliente = repository.findById(id).orElseThrow(() -> new NotFoundException("Cliente não encontrado com id: " + id));
        cliente.setNome(clienteDto.nome());
        cliente.setStatus(clienteDto.status());
        cliente.setTipo(clienteDto.tipoCliente());
        ClienteDadosFiscaisDto dadosFiscaisDto = clienteDto.dadosFiscais();
        ClienteDadosFiscais dadosFiscais = new ClienteDadosFiscais(dadosFiscaisDto.razaoSocial(),
                dadosFiscaisDto.nomeFantasia(),
                dadosFiscaisDto.cnpj() == null ? null : new CNPJ(dadosFiscaisDto.cnpj()),
                dadosFiscaisDto.cpf() == null ? null : new CPF(dadosFiscaisDto.cpf()),
                dadosFiscaisDto.rg() == null ? null : new RG(dadosFiscaisDto.rg()),
                dadosFiscaisDto.inscricaoEstadual(),
                dadosFiscaisDto.inscricaoMunicipal(),
                dadosFiscaisDto.regimeTributario(),
                dadosFiscaisDto.icmsContribuinte(),
                dadosFiscaisDto.aliquotaIcms(),
                dadosFiscaisDto.cnaePrincipal(),
                dadosFiscaisDto.consumidorFinal()
        );
        cliente.setDadosFiscais(dadosFiscais);
        ClienteDadosFinanceirosDto dadosFinanceirosDto = clienteDto.dadosFinanceiros();
        ClienteDadosFinanceiros dadosFinanceiros = new ClienteDadosFinanceiros(
                dadosFinanceirosDto.limiteCredito(),
                dadosFinanceirosDto.limiteDesconto(),
                dadosFinanceirosDto.restricaoFinanceira(),
                dadosFinanceirosDto.protestado()
        );
        cliente.setDadosFinanceiros(dadosFinanceiros);

        ClientePreferenciasDto preferenciasDto = clienteDto.preferencias();
        ClientePreferencias preferencias = new ClientePreferencias(
                preferenciasDto.emailPrincipal(),
                preferenciasDto.emailNfe(),
                preferenciasDto.enviarEmail(),
                preferenciasDto.malaDireta()
        );
        cliente.setPreferencias(preferencias);
        return repository.save(cliente);
    }

    @Override
    public void deletar(Long id) {
        validator.validarId(id);
        repository.deleteById(id);
    }
}
