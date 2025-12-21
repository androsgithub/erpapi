package com.api.erp.features.empresa.application.service;

import com.api.erp.features.empresa.application.dto.CriarEmpresaRequest;
import com.api.erp.features.empresa.application.dto.EmpresaResponse;
import com.api.erp.features.empresa.domain.entity.Empresa;
import com.api.erp.features.empresa.domain.factory.EmpresaConfig;
import com.api.erp.features.empresa.domain.factory.EmpresaFactory;
import com.api.erp.features.empresa.domain.repository.EmpresaRepository;
import com.api.erp.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.shared.domain.exception.NotFoundException;
import com.api.erp.shared.domain.valueobject.Telefone;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriarEmpresaService {
    
    private final EmpresaRepository empresaRepository;
    private final EnderecoRepository enderecoRepository;
    
    public CriarEmpresaService(EmpresaRepository empresaRepository, EnderecoRepository enderecoRepository) {
        this.empresaRepository = empresaRepository;
        this.enderecoRepository = enderecoRepository;
    }
    
    public EmpresaResponse executar(CriarEmpresaRequest request) {
        Empresa empresa = EmpresaFactory.criar(
            request.getNome(),
            request.getCnpj(),
            request.getEmail()
        );
        
        // Configurações adicionais
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) {
            empresa.setTelefone(new Telefone(request.getTelefone()));
        }

        empresa.setRequerAprovacaoGestor(request.isRequerAprovacaoGestor());
        empresa.setRequerEmailCorporativo(request.isRequerEmailCorporativo());
        empresa.setDominiosPermitidos(request.getDominiosPermitidos());
        
        Empresa empresaSalva = empresaRepository.salvar(empresa);
        
        return converterParaResponse(empresaSalva);
    }
    
    public List<EmpresaResponse> buscarTodas() {
        return empresaRepository.buscarTodas()
            .stream()
            .map(this::converterParaResponse)
            .collect(Collectors.toList());
    }
    
    public List<EmpresaResponse> buscarAativas() {
        return empresaRepository.buscarAativas()
            .stream()
            .map(this::converterParaResponse)
            .collect(Collectors.toList());
    }
    
    public EmpresaResponse buscarPorId(String id) {
        Empresa empresa = empresaRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Empresa não encontrada com id: " + id));
        return converterParaResponse(empresa);
    }
    
    public EmpresaResponse buscarPorCnpj(String cnpj) {
        Empresa empresa = empresaRepository.buscarPorCnpj(cnpj)
            .orElseThrow(() -> new NotFoundException("Empresa não encontrada com CNPJ: " + cnpj));
        return converterParaResponse(empresa);
    }
    
    public EmpresaResponse atualizarEmpresa(String id, CriarEmpresaRequest request) {
        Empresa empresa = empresaRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Empresa não encontrada com id: " + id));
        
        empresa.setNome(request.getNome());
        empresa.setCnpj(new com.api.erp.shared.domain.valueobject.CNPJ(request.getCnpj()));
        empresa.setEmail(new com.api.erp.shared.domain.valueobject.Email(request.getEmail()));
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) {
            empresa.setTelefone(new Telefone(request.getTelefone()));
        }
        if (request.getEnderecoId() != null && !request.getEnderecoId().isBlank()) {
            empresa.setEndereco(enderecoRepository.buscarPorId(request.getEnderecoId())
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id: " + request.getEnderecoId())));
        }
        empresa.setRequerAprovacaoGestor(request.isRequerAprovacaoGestor());
        empresa.setRequerEmailCorporativo(request.isRequerEmailCorporativo());
        empresa.setDominiosPermitidos(request.getDominiosPermitidos());
        empresa.setDataAtualizacao(LocalDateTime.now());
        
        empresaRepository.atualizar(empresa);
        
        return converterParaResponse(empresa);
    }
    
    public void deletarEmpresa(String id) {
        empresaRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Empresa não encontrada com id: " + id));
        
        empresaRepository.deletar(id);
    }
    
    public EmpresaConfig obterConfiguracao() {
        // Retorna a configuração da empresa única
        List<Empresa> empresas = empresaRepository.buscarTodas();
        if (empresas.isEmpty()) {
            return null;
        }
        
        Empresa empresa = empresas.get(0);
        EmpresaConfig config = new EmpresaConfig();
        config.setRequerAprovacaoGestor(empresa.isRequerAprovacaoGestor());
        config.setRequerEmailCorporativo(empresa.isRequerEmailCorporativo());
        config.setDominiosPermitidos(empresa.getDominiosPermitidos());
        return config;
    }
    
    private EmpresaResponse converterParaResponse(Empresa empresa) {
        EmpresaResponse response = new EmpresaResponse();
        response.setId(empresa.getId());
        response.setNome(empresa.getNome());
        response.setCnpj(empresa.getCnpj().getValor());
        response.setEmail(empresa.getEmail().getValor());
        response.setTelefone(empresa.getTelefone() != null ? empresa.getTelefone().getValor() : null);
        response.setEnderecoId(empresa.getEndereco() != null ? empresa.getEndereco().getId() : null);
        response.setAtiva(empresa.isAtiva());
        response.setRequerAprovacaoGestor(empresa.isRequerAprovacaoGestor());
        response.setRequerEmailCorporativo(empresa.isRequerEmailCorporativo());
        response.setDominiosPermitidos(empresa.getDominiosPermitidos());
        response.setDataCriacao(empresa.getDataCriacao());
        response.setDataAtualizacao(empresa.getDataAtualizacao());
        return response;
    }
}
