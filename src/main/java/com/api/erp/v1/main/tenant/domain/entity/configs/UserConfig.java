package com.api.erp.v1.main.tenant.domain.entity.configs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
public class UserConfig {

    @Column(name = "user_approval_required", nullable = false)
    private Boolean userApprovalRequired = false;

    @Column(name = "user_corporate_email_required", nullable = false)
    private Boolean userCorporateEmailRequired = false;

    @ElementCollection
    @CollectionTable(
            name = "tb_tenant_domains",
            joinColumns = @JoinColumn(name = "tenant_id")
    )
    private List<String> allowedEmailDomains;

    public boolean isUserApprovalRequired() {
        return Boolean.TRUE.equals(userApprovalRequired);
    }

    public boolean isUserCorporateEmailRequired() {
        return Boolean.TRUE.equals(userCorporateEmailRequired);
    }
}

