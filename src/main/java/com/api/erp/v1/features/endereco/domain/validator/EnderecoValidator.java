package com.api.erp.v1.features.endereco.domain.validator;

import com.api.erp.v1.shared.domain.exception.BusinessException;
import com.api.erp.v1.shared.domain.valueobject.CEP;

public class EnderecoValidator {
    
    public static void validarEndereco(String rua, String numero, String bairro, String cidade, String estado, String cep) {
        validarRua(rua);
        validarNumero(numero);
        validarBairro(bairro);
        validarCidade(cidade);
        validarEstado(estado);
        validarCep(cep);
    }
    
    public static void validarRua(String rua) {
        if (rua == null || rua.isBlank()) {
            throw new BusinessException("Rua é obrigatória");
        }
        if (rua.length() < 3) {
            throw new BusinessException("Rua deve ter no mínimo 3 caracteres");
        }
        if (rua.length() > 255) {
            throw new BusinessException("Rua não pode ter mais de 255 caracteres");
        }
    }
    
    public static void validarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new BusinessException("Número é obrigatório");
        }
    }
    
    public static void validarBairro(String bairro) {
        if (bairro == null || bairro.isBlank()) {
            throw new BusinessException("Bairro é obrigatório");
        }
        if (bairro.length() < 2) {
            throw new BusinessException("Bairro deve ter no mínimo 2 caracteres");
        }
    }
    
    public static void validarCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            throw new BusinessException("Cidade é obrigatória");
        }
        if (cidade.length() < 2) {
            throw new BusinessException("Cidade deve ter no mínimo 2 caracteres");
        }
    }
    
    public static void validarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new BusinessException("Estado é obrigatório");
        }
        if (estado.length() != 2) {
            throw new BusinessException("Estado deve ter exatamente 2 caracteres");
        }
    }
    
    public static void validarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new BusinessException("CEP é obrigatório");
        }
        try {
            new CEP(cep);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("CEP inválido");
        }
    }
}
