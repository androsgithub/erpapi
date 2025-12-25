package com.api.erp.v1.shared.infrastructure.bootstrap;

import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EnderecoSeed {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoSeed.class);

    public Endereco executar(EnderecoRepository repository) {
        if (!repository.findAll().isEmpty()) {
            logger.info("Endereço padrão já existe.");
            return repository.findAll().get(0);
        }

        logger.info("Criando Endereço padrão...");

        Endereco endereco = new Endereco(
                "Avenida Paulista",
                "1000",
                "Centro",
                "São Paulo",
                "SP",
                "01311100"
        );
        endereco.setComplemento("Sala 100");

        return repository.save(endereco);
    }
}

