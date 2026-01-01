package com.api.erp.v1.shared.infrastructure.bootstrap;

import com.api.erp.v1.features.usuario.domain.service.IPasswordEncoder;
import com.api.erp.v1.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.v1.features.usuario.domain.entity.Usuario;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.v1.shared.domain.valueobject.CPF;
import com.api.erp.v1.shared.domain.valueobject.Email;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UsuarioAdminSeed {

    public void executar(
            UsuarioRepository usuarioRepository,
            IPasswordEncoder passwordEncoder) {

        if (usuarioRepository.findByEmail(new Email("admin@empresa.com")).isPresent()) {
            log.info("[USUARIO ADMIN SEED] Usuário Admin já existe.");
            return;
        }

        log.info("[USUARIO ADMIN SEED] Criando Usuário Admin padrão...");

        Usuario admin = Usuario.builder()
                .nomeCompleto("Administrador do Sistema")
                .email(new Email("admin@empresa.com"))
                .cpf(new CPF("11144477735"))
                .senhaHash(passwordEncoder.encode("Admin@123456"))
                .status(StatusUsuario.ATIVO)
                .build();

        usuarioRepository.save(admin);

        log.info("[USUARIO ADMIN SEED] Admin criado: admin@empresa.com | Admin@123456");
    }
}

