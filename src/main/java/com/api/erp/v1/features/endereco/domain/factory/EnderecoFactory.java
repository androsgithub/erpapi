package com.api.erp.v1.features.endereco.domain.factory;

import com.api.erp.v1.features.endereco.domain.entity.Endereco;

public class EnderecoFactory {
    
    public static Endereco criar(String rua, String numero, String bairro, String cidade, String estado, String cep) {
        return new Endereco(rua, numero, bairro, cidade, estado, cep);
    }
    
    public static Endereco criar(Long id, String rua, String numero, String complemento, String bairro, String cidade, String estado, String cep) {
        Endereco endereco = new Endereco(rua, numero, bairro, cidade, estado, cep);
        endereco.setId(id);
        endereco.setComplemento(complemento);
        return endereco;
    }
}
