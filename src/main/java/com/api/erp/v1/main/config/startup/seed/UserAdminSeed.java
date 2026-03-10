package com.api.erp.v1.main.config.startup.seed;

import com.api.erp.v1.main.features.permission.domain.entity.Role;
import com.api.erp.v1.main.features.permission.domain.repository.RoleRepository;
import com.api.erp.v1.main.features.user.domain.entity.StatusUser;
import com.api.erp.v1.main.features.user.domain.entity.User;
import com.api.erp.v1.main.features.user.domain.repository.UserRepository;
import com.api.erp.v1.main.features.user.infrastructure.service.PasswordEncoder;
import com.api.erp.v1.main.shared.domain.valueobject.CPF;
import com.api.erp.v1.main.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * COMPONENT - Seeder de Usuário Administrador
 * 
 * Cria o usuário administrador padrão durante inicialização se não existir.
 * Este usuário tem permissões completas no sistema.
 * 
 * Responsibilities:
 * - Check se admin já existe
 * - Create usuário admin com credenciais padrão
 * - Codificar senha com segurança
 * - Associar usuário à role ADMIN
 * - Logar operação
 * 
 * @author ERP System
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserAdminSeed {

    private static final String ADMIN_EMAIL = "admin@empresa.com";
    private static final String ADMIN_NAME = "Administrador do Sistema";
    private static final String ADMIN_CPF = "11144477735";
    private static final String ENCRYPTED_PASS = "$2a$10$uBJ0lQ.2/j/Mm.w2tVl5DuOQ.WtPsOPn5tyjBZl5BYcSoyGuaJQvm";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public void executar() {
        try {
            if (userRepository.findByEmail(new Email(ADMIN_EMAIL)).isPresent()) {
                log.info("⏭️  Admin user '{}' already exists, skipping creation", ADMIN_EMAIL);
                return;
            }

            log.debug("Creating default Admin user...");

            Role adminRole = roleRepository.findByNome("ADMIN").orElse(null);

            User admin = User.builder()
                    .nomeCompleto(ADMIN_NAME)
                    .email(new Email(ADMIN_EMAIL))
                    .cpf(new CPF(ADMIN_CPF))
                    .senhaHash(ENCRYPTED_PASS)
                    .status(StatusUser.ATIVO)
                    .build();

            if (adminRole != null) admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
            log.info("✅ Admin user created successfully");

            log.info("📧 Email: {} | 🔐 Default password: {} (CHANGE ON FIRST RUN)", ADMIN_EMAIL, ENCRYPTED_PASS);

        } catch (Exception e) {
            log.error("❌ Error creating admin user:", e);
            throw new RuntimeException("Failed to create admin user", e);
        }
    }
}
