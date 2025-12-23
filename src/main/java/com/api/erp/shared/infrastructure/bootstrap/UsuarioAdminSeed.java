package com.api.erp.shared.infrastructure.bootstrap;

import com.api.erp.features.usuario.application.service.PasswordEncoder;
import com.api.erp.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.shared.domain.valueobject.CPF;
import com.api.erp.shared.domain.valueobject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAdminSeed {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioAdminSeed.class);

    public void executar(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {

        if (repository.findByEmail("admin@empresa.com").isPresent()) {
            logger.info("Usuário Admin já existe.");
            return;
        }

        logger.info("Criando Usuário Admin padrão...");

        Usuario admin = Usuario.builder()
                .nomeCompleto("Administrador do Sistema")
                .email(new Email("admin@empresa.com"))
                .cpf(new CPF("11144477735"))
                .senhaHash(passwordEncoder.encode("Admin@123456"))
                .status(StatusUsuario.ATIVO)
                .build();

        repository.save(admin);

        logger.info("Admin criado: admin@empresa.com | Admin@123456");
    }
}

