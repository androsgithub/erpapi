package com.api.erp.v1.features.empresa.application.service;

import com.api.erp.v1.features.empresa.application.dto.EmpresaRequest;
import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.empresa.domain.factory.EmpresaConfig;
import com.api.erp.v1.features.empresa.domain.repository.EmpresaRepository;
import com.api.erp.v1.features.empresa.domain.service.EmpresaService;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EnderecoRepository enderecoRepository;

    public EmpresaServiceImpl(EmpresaRepository empresaRepository, EnderecoRepository enderecoRepository) {
        this.empresaRepository = empresaRepository;
        this.enderecoRepository = enderecoRepository;
    }


    @Override
    public Empresa getDadosEmpresa() {
        return empresaRepository.findById(1L).orElse(null);
    }

    @Override
    public Empresa updateDadosEmpresa(EmpresaRequest empresaRequest) {
        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        empresa.setNome(empresaRequest.nome());
        empresa.setCnpj(empresaRequest.cnpj());
        empresa.setEmail(empresaRequest.email());
        empresa.setTelefone(empresaRequest.telefone());
        Endereco endereco = enderecoRepository.findById(empresaRequest.enderecoId()).orElse(null);
        empresa.setEndereco(endereco);
        empresa.setRequerAprovacaoGestor(empresaRequest.requerAprovacaoGestor());
        empresa.setRequerEmailCorporativo(empresaRequest.requerEmailCorporativo());
        empresa.setDominiosPermitidos(empresaRequest.dominiosPermitidos());
        return empresa;
    }

    @Override
    public EmpresaConfig getEmpresaConfig() {
        Empresa empresa = empresaRepository.findById(1L).orElse(null);
        return empresa.getConfig();
    }
}
