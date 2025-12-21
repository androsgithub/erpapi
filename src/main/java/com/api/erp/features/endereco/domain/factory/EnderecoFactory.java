package com.api.erp.features.endereco.domain.factory;

import com.api.erp.features.endereco.domain.entity.Endereco;
import java.util.UUID;

public class EnderecoFactory {
    
    public static Endereco criar(String rua, String numero, String bairro, String cidade, String estado, String cep) {
        return new Endereco(rua, numero, bairro, cidade, estado, cep);
    }
    
    public static Endereco criar(String id, String rua, String numero, String complemento, String bairro, String cidade, String estado, String cep) {
        Endereco endereco = new Endereco(rua, numero, bairro, cidade, estado, cep);
        endereco.setId(id);
        endereco.setComplemento(complemento);
        return endereco;
    }
}
