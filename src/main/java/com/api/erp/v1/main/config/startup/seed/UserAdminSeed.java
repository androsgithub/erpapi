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
 * Responsabilidades:
 * - Verificar se admin já existe
 * - Criar usuário admin com credenciais padrão
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
    private static final String ADMIN_PASSWORD = "Admin@123456";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public void executar() {
        try {
            if (userRepository.findByEmail(new Email(ADMIN_EMAIL)).isPresent()) {
                log.info("⏭️  Usuário Admin '{}' já existe, pulando criação", ADMIN_EMAIL);
                return;
            }

            log.debug("Criando Usuário Admin padrão...");

            Role adminRole = roleRepository.findByNome("ADMIN").orElse(null);

            User admin = User.builder()
                    .nomeCompleto(ADMIN_NAME)
                    .email(new Email(ADMIN_EMAIL))
                    .cpf(new CPF(ADMIN_CPF))
                    .senhaHash(passwordEncoder.encode(ADMIN_PASSWORD))
                    .status(StatusUser.ATIVO)
                    .build();

            if (adminRole != null) admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
            log.info("✅ Usuário Admin criado com sucesso");

            log.info("📧 Email: {} | 🔐 Senha padrão: {} (ALTERE NA PRIMEIRA EXECUÇÃO)", ADMIN_EMAIL, ADMIN_PASSWORD);

        } catch (Exception e) {
            log.error("❌ Erro ao criar usuário admin:", e);
            throw new RuntimeException("Falha ao criar usuário admin", e);
        }
    }
}
