package com.api.erp.v1.features.cliente.infrastructure.factory;

import com.api.erp.v1.features.cliente.domain.validator.IClienteValidator;
import com.api.erp.v1.features.cliente.infrastructure.validator.NoOpClienteValidator;
import com.api.erp.v1.features.empresa.infrastructure.service.ConfigService;
import com.api.erp.v1.shared.domain.enums.TenantCode;
import com.api.erp.v1.shared.domain.enums.TenantType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ClienteValidatorFactory {

    private final Map<TenantCode, IClienteValidator> validators;
    private final IClienteValidator defaultValidator;
    private final ConfigService configService;

    public ClienteValidatorFactory(
            List<IClienteValidator> validatorList,
            ConfigService configService
    ) {
        this.validators = validatorList.stream()
                .collect(Collectors.toMap(
                        IClienteValidator::getTenantCode,
                        Function.identity()
                ));

        log.info(validators.toString());

        this.defaultValidator = validators.get(TenantCode.DEFAULT);
        this.configService = configService;

        log.info("[CLIENTE/Validator FACTORY] Validadores registrados: {}",
                validators.keySet());
    }

    /**
     * Retorna o validador correto baseado no tenant e nas configurações
     */
    public IClienteValidator create() {
        // Verifica se validação está habilitada
        if (!configService.isClienteValidationEnabled()) {
            log.debug("[CLIENTE/Validator FACTORY] Validação desabilitada, usando NoOpValidator");
            return new NoOpClienteValidator();
        }

        // Verifica se customizações do tenant estão habilitadas
        if (!configService.isClienteTenantCustomizationEnabled()) {
            log.debug("[CLIENTE/Validator FACTORY] Customizações de tenant desabilitadas, usando default");
            return defaultValidator;
        }

        // Resolve validador do tenant atual
        TenantCode tenantCode = configService.getTenantConfig().getTenantCustomCode();
        IClienteValidator validator = validators.getOrDefault(tenantCode, defaultValidator);

        log.debug("[CLIENTE/Validator FACTORY] Usando validador '{}' para tenant '{}'",
                validator.getClass().getSimpleName(), tenantCode);

        return validator;
    }
}