package com.api.erp.v1.features.usuario.infrastructure.decorator;

import com.api.erp.v1.features.usuario.application.dto.request.CreateUsuarioRequest;
import com.api.erp.v1.features.usuario.domain.validator.IUsuarioValidator;
import com.api.erp.v1.shared.domain.exception.BusinessException;
import com.api.erp.v1.shared.infrastructure.security.BearerTokenFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EmailCorporativoValidatorDecorator implements IUsuarioValidator {

    private static final Logger logger = LoggerFactory.getLogger(BearerTokenFilter.class);
    private final IUsuarioValidator wrapped;
    private final List<String> dominiosPermitidos;
    
    public EmailCorporativoValidatorDecorator(IUsuarioValidator wrapped, List<String> dominiosPermitidos) {
        this.wrapped = wrapped;
        this.dominiosPermitidos = dominiosPermitidos;
    }
    
    @Override
    public void validar(CreateUsuarioRequest request) {
        // Executa validação base
        wrapped.validar(request);
        
        // Adiciona validação de domínio corporativo
        String email = request.email();
        String dominio = email.substring(email.indexOf('@')+1);
        
        boolean dominioValido = dominiosPermitidos.stream()
            .anyMatch(d -> dominio.equalsIgnoreCase(d));

        logger.info(dominio+" "+Boolean.toString(dominioValido));

        if (!dominioValido) {
            throw new BusinessException("Email deve ser de domínio corporativo: " + dominiosPermitidos);
        }
    }
}
