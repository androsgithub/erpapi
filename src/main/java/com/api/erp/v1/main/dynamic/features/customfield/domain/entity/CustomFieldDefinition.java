package com.api.erp.v1.main.dynamic.features.customfield.domain.entity;

import com.api.erp.v1.main.shared.domain.entity.TenantScopeEntity;
import com.api.erp.v1.main.shared.domain.enums.CustomFieldType;
import com.api.erp.v1.main.shared.infrastructure.persistence.converters.JpaJsonConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "TB_custom_field_definition")
@SQLDelete(sql = "UPDATE TB_custom_field_definition SET deleted = true, deleted_at = now() WHERE id = ?")
public class CustomFieldDefinition extends TenantScopeEntity {

    @Column(name = "target")
    private String target;

    @Column(name = "field_key")
    private String fieldKey;

    @Column(name = "label")
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type")
    private CustomFieldType fieldType;

    @Column(name = "required")
    private Boolean required;

    @Column(name = "field_order")
    private Integer fieldOrder;

    @Column(name = "metadata")
    @Convert(converter = JpaJsonConverter.class)
    private Map<String, Object> metadata;

    @Column(name = "active")
    private Boolean active;
}

