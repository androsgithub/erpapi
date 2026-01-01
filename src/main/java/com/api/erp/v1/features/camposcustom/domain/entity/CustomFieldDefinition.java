package com.api.erp.v1.features.camposcustom.domain.entity;

import com.api.erp.v1.shared.domain.enums.CustomFieldType;
import com.api.erp.v1.shared.infrastructure.persistence.converters.JpaJsonConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "custom_field_definition")
@Getter
@Setter
public class CustomFieldDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tenantId;

    private String tableName;

    private String fieldKey;

    private String label;

    @Enumerated(EnumType.STRING)
    private CustomFieldType fieldType;

    private Boolean required;

    private Integer fieldOrder;

    @Column(columnDefinition = "json")
    @Convert(converter = JpaJsonConverter.class)
    private Map<String, Object> metadata;


    private Boolean active;
}

