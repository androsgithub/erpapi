package com.api.erp.v1.main.features.permissao.infrastructure.decorator;

import com.api.erp.v1.main.features.permissao.domain.service.IPermissaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Decorator de validação para o serviço de Permissão.
 * Responsável por validar parâmetros antes de processar a lógica de permissões.
 */
@Slf4j
@RequiredArgsConstructor
public class ValidationDecoratorPermissaoService implements IPermissaoService {

    private final IPermissaoService delegate;

    @Override
    public boolean hasPermission(Long usuarioId, String permissaoCodigo, Map<String, String> contexto) {
        log.debug("[PERMISSAO VALIDATION] Validando parametros - usuarioId: {}, permissaoCodigo: {}", 
                usuarioId, permissaoCodigo);

        validarParametros(usuarioId, permissaoCodigo);

        log.debug("[PERMISSAO VALIDATION] Parametros validados com sucesso");
        return delegate.hasPermission(usuarioId, permissaoCodigo, contexto);
    }

    private void validarParametros(Long usuarioId, String permissaoCodigo) {
        if (usuarioId == null || usuarioId <= 0) {
            log.warn("[PERMISSAO VALIDATION] usuarioId inválido: {}", usuarioId);
            throw new IllegalArgumentException("usuarioId deve ser maior que 0");
        }

        if (permissaoCodigo == null || permissaoCodigo.isBlank()) {
            log.warn("[PERMISSAO VALIDATION] permissaoCodigo inválido: {}", permissaoCodigo);
            throw new IllegalArgumentException("permissaoCodigo não pode ser nulo ou vazio");
        }
    }
}
