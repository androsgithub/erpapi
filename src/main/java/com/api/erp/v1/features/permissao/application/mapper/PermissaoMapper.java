package com.api.erp.v1.features.permissao.application.mapper;

import com.api.erp.v1.features.permissao.application.dto.response.PermissaoResponse;
import com.api.erp.v1.features.permissao.domain.entity.Permissao;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissaoMapper {

    PermissaoResponse toResponse(Permissao permissao);

    List<PermissaoResponse> toResponseList(List<Permissao> permissoes);
}
