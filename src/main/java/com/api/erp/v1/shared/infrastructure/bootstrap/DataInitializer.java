package com.api.erp.v1.shared.infrastructure.bootstrap;

import com.api.erp.v1.features.empresa.domain.repository.EmpresaRepository;
import com.api.erp.v1.features.endereco.domain.entity.Endereco;
import com.api.erp.v1.features.endereco.domain.repository.EnderecoRepository;
import com.api.erp.v1.features.unidademedida.domain.repository.UnidadeMedidaRepository;
import com.api.erp.v1.features.usuario.domain.service.IPasswordEncoder;
import com.api.erp.v1.features.usuario.domain.repository.UsuarioRepository;
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
            IPasswordEncoder passwordEncoder,
            EnderecoSeed enderecoSeed,
            EmpresaSeed empresaSeed,
            UsuarioAdminSeed usuarioAdminSeed,
            UnidadeMedidaSeed unidadeMedidaSeed,
            PermissaoSeed permissaoSeed
    ) {
        return args -> {
            Endereco endereco = enderecoSeed.executar(enderecoRepository);
            empresaSeed.executar(empresaRepository, endereco);
            permissaoSeed.executar();
            unidadeMedidaSeed.executar(unidadeMedidaRepository);
            usuarioAdminSeed.executar(usuarioRepository, passwordEncoder);
        };
    }
}

