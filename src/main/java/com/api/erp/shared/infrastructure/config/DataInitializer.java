package com.api.erp.shared.infrastructure.config;

import com.api.erp.features.endereco.domain.entity.Endereco;
import com.api.erp.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.features.empresa.domain.entity.Empresa;
import com.api.erp.features.empresa.domain.repository.EmpresaRepository;
import com.api.erp.features.usuario.domain.entity.StatusUsuario;
import com.api.erp.features.usuario.domain.entity.Usuario;
import com.api.erp.features.usuario.domain.repository.UsuarioRepository;
import com.api.erp.features.usuario.application.service.PasswordEncoder;
import com.api.erp.shared.domain.valueobject.CPF;
import com.api.erp.shared.domain.valueobject.Email;
import com.api.erp.shared.domain.valueobject.CNPJ;
import com.api.erp.shared.domain.valueobject.Telefone;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;

@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Bean
    public CommandLineRunner initializeData(
            EnderecoRepository enderecoRepository,
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository, 
            PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // Inicializa Endereço
                Endereco endereco = null;
                if (enderecoRepository.buscarTodos().isEmpty()) {
                    logger.info("Criando Endereço padrão...");
                    
                    endereco = new Endereco(
                        "Avenida Paulista",
                        "1000",
                        "Centro",
                        "São Paulo",
                        "SP",
                        "01311100"
                    );
                    endereco.setComplemento("Sala 100");
                    
                    endereco = enderecoRepository.salvar(endereco);
                    logger.info("Endereço padrão criado com sucesso!");
                } else {
                    endereco = enderecoRepository.buscarTodos().get(0);
                    logger.info("Endereço já existe no sistema.");
                }
                
                // Inicializa Empresa
                if (empresaRepository.buscarTodas().isEmpty()) {
                    logger.info("Criando Empresa padrão...");
                    
                    Empresa empresa = new Empresa();
                    empresa.setNome("Empresa Padrão");
                    empresa.setCnpj(new CNPJ("11222333000181"));
                    empresa.setEmail(new Email("empresa@example.com"));
                    empresa.setTelefone(new Telefone("1133334444"));
                    empresa.setEndereco(endereco);
                    empresa.setAtiva(true);
                    empresa.setRequerAprovacaoGestor(false);
                    empresa.setRequerEmailCorporativo(false);
                    empresa.setDominiosPermitidos(Arrays.asList("example.com", "empresa.com"));
                    
                    empresaRepository.salvar(empresa);
                    logger.info("Empresa padrão criada com sucesso!");
                } else {
                    logger.info("Empresa já existe no sistema.");
                }
                
                // Inicializa Usuário Admin
                if (usuarioRepository.findByEmail("admin@empresa.com").isEmpty()) {
                    logger.info("Criando usuário Admin padrão...");
                    
                    Usuario admin = Usuario.builder()
                            .nomeCompleto("Administrador do Sistema")
                            .email(new Email("admin@empresa.com"))
                            .cpf(new CPF("11144477735"))
                            .senhaHash(passwordEncoder.encode("Admin@123456"))
                            .status(StatusUsuario.ATIVO)
                            .build();
                    
                    usuarioRepository.save(admin);
                    logger.info("Usuário Admin criado com sucesso!");
                    logger.info("Email: admin@empresa.com");
                    logger.info("Senha: Admin@123456");
                } else {
                    logger.info("Usuário Admin já existe no sistema.");
                }
            } catch (Exception e) {
                logger.error("Erro ao inicializar dados: ", e);
            }
        };
    }
}
