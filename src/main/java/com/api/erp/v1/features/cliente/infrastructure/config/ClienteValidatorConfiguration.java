package com.api.erp.v1.features.cliente.infrastructure.config;

import com.api.erp.v1.features.cliente.domain.service.IClienteService;
import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.features.cliente.infrastructure.factory.ClienteValidatorFactory;
import com.api.erp.v1.features.cliente.infrastructure.proxy.ClienteValidatorHolder;
import com.api.erp.v1.features.cliente.infrastructure.proxy.ClienteValidatorProxy;
import com.api.erp.v1.features.cliente.infrastructure.validator.NoOpClienteValidator;
import com.api.erp.v1.features.empresa.infrastructure.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Configuration
@Order(1)
public class ClienteValidatorConfiguration {

    @Bean
    public ClienteValidatorFactory clienteValidatorFactory(
            List<IClienteValidator> validators,
            ConfigService configService) {

        log.info("[CLIENTE/Validator CONFIG] Inicializando ClienteValidatorFactory");
        return new ClienteValidatorFactory(validators, configService);
    }

    @Bean
    public ClienteValidatorHolder clienteValidatorHolder() {
        log.info("[CLIENTE/Validator CONFIG] Inicializando ClienteValidatorHolder");
        return new ClienteValidatorHolder(
                new AtomicReference<>(new NoOpClienteValidator())
        );
    }

    @Bean
    @Primary
    public IClienteValidator clienteValidator(ClienteValidatorHolder holder) {
        log.info("[CLIENTE/Validator CONFIG] Inicializando ClienteValidatorProxy");
        return new ClienteValidatorProxy(holder);
    }

    @Bean
    public ClienteValidatorConfiguration.ProxyInitializer clienteValidatorProxyInitializer(
            ClienteValidatorFactory factory,
            ClienteValidatorHolder holder) {

        return new ClienteValidatorConfiguration.ProxyInitializer(factory, holder);
    }

    @Slf4j
    public static class ProxyInitializer {
        public ProxyInitializer(ClienteValidatorFactory factory, ClienteValidatorHolder holder) {
            log.info("[CLIENTE/Validator CONFIG] Inicializando ClienteValidatorProxy com decorators iniciais");
            var initialValidator = factory.create();
            holder.updateValidator(initialValidator);
            log.info("[CLIENTE/Validator CONFIG] ClienteValidatorProxy inicializado com sucesso");
        }
    }
}
