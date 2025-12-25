package com.api.erp.v1.shared.infrastructure.bootstrap;

import com.api.erp.v1.features.usuario.application.service.PasswordEncoder;
import com.api.erp.v1.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.v1.shared.domain.valueobject.CPF;
import com.api.erp.v1.shared.domain.valueobject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAdminSeed {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioAdminSeed.class);

    public void executar(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        if (usuarioRepository.findByEmail(new Email("admin@empresa.com")).isPresent()) {
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

        usuarioRepository.save(admin);

        logger.info("Admin criado: admin@empresa.com | Admin@123456");
    }
}

