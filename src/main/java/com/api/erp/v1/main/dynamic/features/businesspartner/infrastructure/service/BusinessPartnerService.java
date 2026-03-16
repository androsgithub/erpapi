package com.api.erp.v1.main.dynamic.features.businesspartner.infrastructure.service;

import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerDadosFinanceirosDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerDadosFiscaisDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.BusinessPartnerPreferenciasDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.application.dto.request.CreateBusinessPartnerDto;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartner;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerDadosFinanceiros;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerDadosFiscais;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.entity.BusinessPartnerPreferencias;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.repository.BusinessPartnerRepository;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.service.IBusinessPartnerService;
import com.api.erp.v1.main.dynamic.features.businesspartner.domain.validator.IBusinessPartnerValidator;
import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
import com.api.erp.v1.main.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.RG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BusinessPartnerService implements IBusinessPartnerService {

    private final BusinessPartnerRepository repository;
    @Qualifier("businessPartnerValidatorProxy")
    private final IBusinessPartnerValidator validator;

    @Autowired
    public BusinessPartnerService(BusinessPartnerRepository repository, IBusinessPartnerValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    @Cacheable(value = "business-partners", keyGenerator = "principalKeyGenerator")
    public Page<BusinessPartner> pegarTodos(Pageable pageable) {
        validator.validarPageable(pageable);
        return repository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "business-partner", keyGenerator = "principalKeyGenerator")
    public BusinessPartner pegarPorId(Long id) {
        validator.validarId(id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("BusinessPartner não encontrado com id: " + id));
    }

    @Override
    public BusinessPartner criar(CreateBusinessPartnerDto businessPartnerDto) {
        validator.validarCriacao(businessPartnerDto);
        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setNome(businessPartnerDto.nome());
        businessPartner.setStatus(businessPartnerDto.status());
        businessPartner.setTipo(businessPartnerDto.tipoBusinessPartner());
        BusinessPartnerDadosFiscaisDto dadosFiscaisDto = businessPartnerDto.dadosFiscais();
        BusinessPartnerDadosFiscais dadosFiscais = new BusinessPartnerDadosFiscais(
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
        businessPartner.setDadosFiscais(dadosFiscais);

        BusinessPartnerDadosFinanceirosDto dadosFinanceirosDto = businessPartnerDto.dadosFinanceiros();
        BusinessPartnerDadosFinanceiros dadosFinanceiros = new BusinessPartnerDadosFinanceiros(
                dadosFinanceirosDto.limiteCredito(),
                dadosFinanceirosDto.limiteDesconto(),
                dadosFinanceirosDto.restricaoFinanceira(),
                dadosFinanceirosDto.protestado()
        );
        businessPartner.setDadosFinanceiros(dadosFinanceiros);

        BusinessPartnerPreferenciasDto preferenciasDto = businessPartnerDto.preferencias();
        BusinessPartnerPreferencias preferencias = new BusinessPartnerPreferencias(
                preferenciasDto.emailPrincipal(),
                preferenciasDto.emailNfe(),
                preferenciasDto.enviarEmail(),
                preferenciasDto.malaDireta()
        );
        businessPartner.setPreferencias(preferencias);

        return repository.save(businessPartner);
    }

    @Override
    public BusinessPartner atualizar(Long id, CreateBusinessPartnerDto businessPartnerDto) {
        validator.validarAtualizacao(id, businessPartnerDto);
        BusinessPartner businessPartner = repository.findById(id).orElseThrow(() -> new NotFoundException("BusinessPartner não encontrado com id: " + id));
        businessPartner.setNome(businessPartnerDto.nome());
        businessPartner.setStatus(businessPartnerDto.status());
        businessPartner.setTipo(businessPartnerDto.tipoBusinessPartner());
        BusinessPartnerDadosFiscaisDto dadosFiscaisDto = businessPartnerDto.dadosFiscais();
        BusinessPartnerDadosFiscais dadosFiscais = new BusinessPartnerDadosFiscais(dadosFiscaisDto.razaoSocial(),
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
        businessPartner.setDadosFiscais(dadosFiscais);
        BusinessPartnerDadosFinanceirosDto dadosFinanceirosDto = businessPartnerDto.dadosFinanceiros();
        BusinessPartnerDadosFinanceiros dadosFinanceiros = new BusinessPartnerDadosFinanceiros(
                dadosFinanceirosDto.limiteCredito(),
                dadosFinanceirosDto.limiteDesconto(),
                dadosFinanceirosDto.restricaoFinanceira(),
                dadosFinanceirosDto.protestado()
        );
        businessPartner.setDadosFinanceiros(dadosFinanceiros);

        BusinessPartnerPreferenciasDto preferenciasDto = businessPartnerDto.preferencias();
        BusinessPartnerPreferencias preferencias = new BusinessPartnerPreferencias(
                preferenciasDto.emailPrincipal(),
                preferenciasDto.emailNfe(),
                preferenciasDto.enviarEmail(),
                preferenciasDto.malaDireta()
        );
        businessPartner.setPreferencias(preferencias);
        return repository.save(businessPartner);
    }

    @Override
    public void deletar(Long id) {
        validator.validarId(id);
        repository.deleteById(id);
    }
}
