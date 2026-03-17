package com.api.erp.v1.main.master.tenant.application.dto.request.update;

/**
 * UNIFIED CONFIG UPDATE - Consolidação de todos os 6 métodos antigos de config em 1
 * Processa apenas os campos que foram preenchidos (não-null)
 *
 * Fields:
 * - appName: Nome da aplicação
 * - appDescription: Descrição da aplicação
 * - supportEmail: Email de suporte
 * - multiBranch: Permite múltiplas filiais?
 * - allowApiAccess: Permite acesso via API?
 * - whiteLabel: Modo white label?
 * - maintenanceMode: Modo manutenção?
 * - businesspartnerValidation: Validação de parceiros de negócio?
 * - userApproval: Requer aprovação de usuários?
 * - permissionCache: Cache de permissões?
 */
public record UnifiedTenantConfigRequest(
        String appName,
        String appDescription,
        String supportEmail,
        Boolean multiBranch,
        Boolean allowApiAccess,
        Boolean whiteLabel,
        Boolean maintenanceMode,
        Boolean businesspartnerValidation,
        Boolean userApproval,
        Boolean permissionCache
) {}
