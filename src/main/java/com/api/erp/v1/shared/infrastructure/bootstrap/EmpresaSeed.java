package com.api.erp.v1.shared.infrastructure.bootstrap;

import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.empresa.domain.repository.EmpresaRepository;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmpresaSeed {

    private static final Logger logger = LoggerFactory.getLogger(EmpresaSeed.class);

    public void executar(EmpresaRepository repository, Endereco endereco) {
        if (!repository.findById(1L).isEmpty()) {
            logger.info("Empresa padrão já existe.");
            return;
        }

        logger.info("Criando Empresa padrão...");

        Empresa empresa = new Empresa();
        empresa.setNome("Empresa Padrão");
        empresa.setCnpj(new CNPJ("11222333000181"));
        empresa.setEmail(new Email("empresa@example.com"));
        empresa.setTelefone(new Telefone("1133334444"));
        empresa.setEndereco(endereco);
        empresa.setAtiva(true);
        empresa.setRequerAprovacaoGestor(true);
        empresa.setRequerEmailCorporativo(false);
        empresa.setDominiosPermitidos(List.of("example.com", "empresa.com"));
        repository.save(empresa);
    }
}

