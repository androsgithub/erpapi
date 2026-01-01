package com.api.erp.v1.features.contato.infrastructure.decorator;

import com.api.erp.v1.features.contato.application.dto.CreateContatoRequest;
import com.api.erp.v1.features.contato.domain.entity.Contato;
import com.api.erp.v1.features.contato.domain.service.IContatoService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Exemplo de Decorator Customizado para validação de formato
 * 
 * Este decorator valida o formato dos valores de contato de acordo com seu tipo:
 * - Email: valida padrão de email
 * - Telefone/Celular: valida padrão de telefone brasileiro
 * - WhatsApp: valida padrão de telefone
 * - Etc.
 * 
 * Para usar, adicione à factory:
 * <pre>
 * service = new FormatValidationDecoratorContatoService(service);
 * </pre>
 */
@Slf4j
public class FormatValidationDecoratorContatoService implements IContatoService {

    private final IContatoService service;

    // Padrões de validação
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern TELEFONE_BR_PATTERN = 
        Pattern.compile("^\\(?\\d{2}\\)?\\s?\\d{4,5}[-\\s]?\\d{4}$");
    
    private static final Pattern URL_PATTERN = 
        Pattern.compile("^https?://[\\w.-]+\\.[a-z]{2,}(/.*)?$");

    public FormatValidationDecoratorContatoService(IContatoService service) {
        this.service = service;
    }

    @Override
    public Contato criar(CreateContatoRequest request) {
        validarFormato(request.tipo(), request.valor());
        return service.criar(request);
    }

    @Override
    public Contato buscarPorId(Long id) {
        return service.buscarPorId(id);
    }

    @Override
    public List<Contato> buscarTodos() {
        return service.buscarTodos();
    }

    @Override
    public List<Contato> buscarAtivos() {
        return service.buscarAtivos();
    }

    @Override
    public List<Contato> buscarInativos() {
        return service.buscarInativos();
    }

    @Override
    public List<Contato> buscarPorTipo(String tipo) {
        return service.buscarPorTipo(tipo);
    }

    @Override
    public Contato buscarPrincipal() {
        return service.buscarPrincipal();
    }

    @Override
    public Contato atualizar(Long id, CreateContatoRequest request) {
        validarFormato(request.tipo(), request.valor());
        return service.atualizar(id, request);
    }

    @Override
    public Contato ativar(Long id) {
        return service.ativar(id);
    }

    @Override
    public Contato desativar(Long id) {
        return service.desativar(id);
    }

    @Override
    public void deletar(Long id) {
        service.deletar(id);
    }

    /**
     * Valida o formato do valor de acordo com o tipo de contato
     */
    private void validarFormato(String tipo, String valor) {
        if (tipo == null || valor == null) {
            return; // Deixar para validador anterior
        }

        switch (tipo.toUpperCase()) {
            case "EMAIL":
                if (!EMAIL_PATTERN.matcher(valor).matches()) {
                    throw new IllegalArgumentException("Email inválido: " + valor);
                }
                log.debug("[FORMAT] Email validado: {}", ocultarValor(valor));
                break;

            case "TELEFONE":
            case "CELULAR":
            case "WHATSAPP":
                if (!TELEFONE_BR_PATTERN.matcher(valor).matches()) {
                    throw new IllegalArgumentException("Telefone brasileiro inválido: " + valor);
                }
                log.debug("[FORMAT] Telefone validado: {}", ocultarValor(valor));
                break;

            case "WEBSITE":
                if (!URL_PATTERN.matcher(valor).matches()) {
                    throw new IllegalArgumentException("URL inválida: " + valor);
                }
                log.debug("[FORMAT] URL validada: {}", ocultarValor(valor));
                break;

            default:
                log.debug("[FORMAT] Tipo {} não tem validação de formato específica", tipo);
        }
    }

    /**
     * Oculta parte do valor para segurança nos logs
     */
    private String ocultarValor(String valor) {
        if (valor == null || valor.length() < 4) {
            return "***";
        }
        return valor.substring(0, 3) + "***" + valor.substring(valor.length() - 2);
    }
}
