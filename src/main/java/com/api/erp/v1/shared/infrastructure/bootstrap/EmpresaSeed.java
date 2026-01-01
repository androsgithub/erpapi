package com.api.erp.v1.shared.infrastructure.bootstrap;

import com.api.erp.v1.features.empresa.domain.entity.Empresa;
import com.api.erp.v1.features.empresa.domain.entity.EmpresaDadosFiscais;
import com.api.erp.v1.features.empresa.domain.repository.EmpresaRepository;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.shared.domain.enums.ContribuinteICMS;
import com.api.erp.v1.shared.domain.enums.RegimeTributario;
import com.api.erp.v1.shared.domain.valueobject.CNPJ;
import com.api.erp.v1.shared.domain.valueobject.Email;
import com.api.erp.v1.shared.domain.valueobject.Telefone;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EmpresaSeed {

    public void executar(EmpresaRepository repository, Endereco endereco) {
        if (!repository.findById(1L).isEmpty()) {
            log.info("[EMPRESA SEED] Empresa padrão já existe.");
            return;
        }
        log.info("[EMPRESA SEED] Criando Empresa padrão...");

        Empresa empresa = new Empresa();
        empresa.setNome("Empresa Padrão");
        empresa.setEmail(new Email("empresa@example.com"));
        empresa.setTelefone(new Telefone("1133334444"));
        empresa.setAtiva(true);

        EmpresaDadosFiscais empresaDadosFiscais = new EmpresaDadosFiscais();
        empresaDadosFiscais.setCnpj(new CNPJ("11222333000181"));
        empresaDadosFiscais.setRazaoSocial("Empresa Padrão - Razão Social");
        empresaDadosFiscais.setContribuinteICMS(ContribuinteICMS.CONTRIBUINTE);
        empresaDadosFiscais.setRegimeTributario(RegimeTributario.SIMPLES_NACIONAL);

        empresa.setDadosFiscais(empresaDadosFiscais);
        empresa.setEndereco(endereco);
        repository.save(empresa);

        log.info("[EMPRESA SEED] Empresa criada.");
    }
}

