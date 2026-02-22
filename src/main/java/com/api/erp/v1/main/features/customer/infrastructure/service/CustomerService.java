package com.api.erp.v1.main.features.customer.infrastructure.service;

import com.api.erp.v1.main.features.customer.application.dto.CustomerDadosFinanceirosDto;
import com.api.erp.v1.main.features.customer.application.dto.CustomerDadosFiscaisDto;
import com.api.erp.v1.main.features.customer.application.dto.CustomerPreferenciasDto;
import com.api.erp.v1.main.features.customer.application.dto.request.CreateCustomerDto;
import com.api.erp.v1.main.features.customer.domain.entity.Customer;
import com.api.erp.v1.main.features.customer.domain.entity.CustomerDadosFinanceiros;
import com.api.erp.v1.main.features.customer.domain.entity.CustomerDadosFiscais;
import com.api.erp.v1.main.features.customer.domain.entity.CustomerPreferencias;
import com.api.erp.v1.main.features.customer.domain.repository.CustomerRepository;
import com.api.erp.v1.main.features.customer.domain.service.ICustomerService;
import com.api.erp.v1.main.features.customer.domain.validator.ICustomerValidator;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.RG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository repository;
    @Qualifier("customerValidatorProxy")
    private final ICustomerValidator validator;

    @Autowired
    public CustomerService(CustomerRepository repository, ICustomerValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public Page<Customer> pegarTodos(Pageable pageable) {
        validator.validarPageable(pageable);
        return repository.findAll(pageable);
    }

    @Override
    public Customer pegarPorId(Long id) {
        validator.validarId(id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Customer não encontrado com id: " + id));
    }

    @Override
    public Customer criar(CreateCustomerDto customerDto) {
        validator.validarCriacao(customerDto);
        Customer customer = new Customer();
        customer.setNome(customerDto.nome());
        customer.setStatus(customerDto.status());
        customer.setTipo(customerDto.tipoCustomer());
        CustomerDadosFiscaisDto dadosFiscaisDto = customerDto.dadosFiscais();
        CustomerDadosFiscais dadosFiscais = new CustomerDadosFiscais(
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
        customer.setDadosFiscais(dadosFiscais);

        CustomerDadosFinanceirosDto dadosFinanceirosDto = customerDto.dadosFinanceiros();
        CustomerDadosFinanceiros dadosFinanceiros = new CustomerDadosFinanceiros(
                dadosFinanceirosDto.limiteCredito(),
                dadosFinanceirosDto.limiteDesconto(),
                dadosFinanceirosDto.restricaoFinanceira(),
                dadosFinanceirosDto.protestado()
        );
        customer.setDadosFinanceiros(dadosFinanceiros);

        CustomerPreferenciasDto preferenciasDto = customerDto.preferencias();
        CustomerPreferencias preferencias = new CustomerPreferencias(
                preferenciasDto.emailPrincipal(),
                preferenciasDto.emailNfe(),
                preferenciasDto.enviarEmail(),
                preferenciasDto.malaDireta()
        );
        customer.setPreferencias(preferencias);

        return repository.save(customer);
    }

    @Override
    public Customer atualizar(Long id, CreateCustomerDto customerDto) {
        validator.validarAtualizacao(id, customerDto);
        Customer customer = repository.findById(id).orElseThrow(() -> new NotFoundException("Customer não encontrado com id: " + id));
        customer.setNome(customerDto.nome());
        customer.setStatus(customerDto.status());
        customer.setTipo(customerDto.tipoCustomer());
        CustomerDadosFiscaisDto dadosFiscaisDto = customerDto.dadosFiscais();
        CustomerDadosFiscais dadosFiscais = new CustomerDadosFiscais(dadosFiscaisDto.razaoSocial(),
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
        customer.setDadosFiscais(dadosFiscais);
        CustomerDadosFinanceirosDto dadosFinanceirosDto = customerDto.dadosFinanceiros();
        CustomerDadosFinanceiros dadosFinanceiros = new CustomerDadosFinanceiros(
                dadosFinanceirosDto.limiteCredito(),
                dadosFinanceirosDto.limiteDesconto(),
                dadosFinanceirosDto.restricaoFinanceira(),
                dadosFinanceirosDto.protestado()
        );
        customer.setDadosFinanceiros(dadosFinanceiros);

        CustomerPreferenciasDto preferenciasDto = customerDto.preferencias();
        CustomerPreferencias preferencias = new CustomerPreferencias(
                preferenciasDto.emailPrincipal(),
                preferenciasDto.emailNfe(),
                preferenciasDto.enviarEmail(),
                preferenciasDto.malaDireta()
        );
        customer.setPreferencias(preferencias);
        return repository.save(customer);
    }

    @Override
    public void deletar(Long id) {
        validator.validarId(id);
        repository.deleteById(id);
    }
}
