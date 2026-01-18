package com.api.erp.v1.observability.application.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation para instruir o AOP a rastrear fluxo de execução.
 * Deve ser aplicada em métodos de @Service ou @UseCase.
 * 
 * Exemplo:
 * @TrackFlow("CREATE_USER")
 * public User create(CreateUserCommand cmd) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackFlow {

    /**
     * Nome do passo de fluxo a ser rastreado.
     * Deve ser uma constante legível e imutável.
     */
    String value();
}
