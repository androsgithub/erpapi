package com.api.erp.shared.infrastructure.bootstrap;

import com.api.erp.features.empresa.domain.repository.EmpresaRepository;
import com.api.erp.features.endereco.domain.entity.Endereco;
import com.api.erp.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import com.api.erp.features.usuario.application.service.PasswordEncoder;
import com.api.erp.features.usuario.domain.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(
            EnderecoRepository enderecoRepository,
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            UnidadeMedidaRepository unidadeMedidaRepository,
            PasswordEncoder passwordEncoder,
            EnderecoSeed enderecoSeed,
            EmpresaSeed empresaSeed,
            UsuarioAdminSeed usuarioAdminSeed,
            UnidadeMedidaSeed unidadeMedidaSeed) {

        return args -> {
            Endereco endereco = enderecoSeed.executar(enderecoRepository);

            empresaSeed.executar(empresaRepository, endereco);
            usuarioAdminSeed.executar(usuarioRepository, passwordEncoder);
            unidadeMedidaSeed.executar(unidadeMedidaRepository);
        };
    }
}

