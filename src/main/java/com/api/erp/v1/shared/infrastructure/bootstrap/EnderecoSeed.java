package com.api.erp.v1.shared.infrastructure.bootstrap;

import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnderecoSeed {

    public Endereco executar(EnderecoRepository repository) {
        if (!repository.findAll().isEmpty()) {
            log.info("[ENDERECO SEED] Endereço padrão já existe.");
            return repository.findAll().get(0);
        }

        log.info("[ENDERECO SEED] Criando Endereço padrão...");

        Endereco endereco = new Endereco(
                "Avenida Paulista",
                "1000",
                "Centro",
                "São Paulo",
                "SP",
                "01311100",
                null
        );
        endereco.setComplemento("Sala 100");

        Endereco _end = repository.save(endereco);
        log.info("[ENDERECO SEED] Endereço criado.");
        return _end;
    }
}

