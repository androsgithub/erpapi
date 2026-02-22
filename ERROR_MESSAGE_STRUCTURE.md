# ErrorMessage Structure - Refactoring Plan

## Objetivo
Criar ErrorMessages específicas por domínio/feature para melhor organização e evitar conflitos de codes.

## Estrutura Proposta

### 1. **TenantErrorMessage** (TENANT_XXX)
Base do sistema, gerenciamento multi-tenant
```
TENANT_001 - Tenant not found or inactive
TENANT_002 - Datasource not configured  
TENANT_003 - Datasource connection failed
TENANT_004 - Datasource already exists
TENANT_005 - Invalid tenant configuration
```

### 2. **UserErrorMessage** (USER_XXX)
Autenticação, autorização, gerenciamento de usuários
```
USER_001 - User not found
USER_002 - Email already registered
USER_003 - CPF already registered
USER_004 - Invalid email or password
USER_005 - Password requirements not met
USER_006 - Permission not found
USER_007 - Role not found
USER_008 - Role not linked to user
USER_009 - User not pending approval
```

### 3. **ProductErrorMessage** (PRODUCT_XXX)
Gestão de products
```
PRODUCT_001 - Product not found
PRODUCT_002 - Product code already exists
PRODUCT_003 - Circular composition
PRODUCT_004 - Product not manufactureable
PRODUCT_005 - Invalid composition quantity
PRODUCT_006 - Product not active
```

### 4. **CustomerErrorMessage** (CUSTOMER_XXX)
Gestão de clientes
```
CUSTOMER_001 - Customer not found
CUSTOMER_002 - Invalid customer data
```

### 5. **AddressErrorMessage** (ADDRESS_XXX)
Gestão de endereços
```
ADDRESS_001 - Address not found
ADDRESS_002 - Invalid address data
ADDRESS_003 - Invalid postal code
```

### 6. **ContactErrorMessage** (CONTACT_XXX)
Gestão de contatos
```
CONTACT_001 - Contact not found
CONTACT_002 - Invalid contact type
CONTACT_003 - Invalid contact value
```

### 7. **PermissionErrorMessage** (PERMISSION_XXX)
Controle de permissões e acesso
```
PERMISSION_001 - Access denied
PERMISSION_002 - Permission not found
PERMISSION_003 - Invalid permission configuration
```

### 8. **FornecedorErrorMessage** (FORNECEDOR_XXX)
Gestão de fornecedores/suppliers
```
FORNECEDOR_001 - Supplier not found
FORNECEDOR_002 - Supplier already registered
```

### 9. **CustomFieldErrorMessage** (CUSTOMFIELD_XXX)
Campos customizáveis
```
CUSTOMFIELD_001 - Field not found
CUSTOMFIELD_002 - Field already registered
CUSTOMFIELD_003 - Invalid field configuration
```

### 10. **MeasureUnitErrorMessage** (UNIDADE_XXX)
Unidades de medida
```
UNIDADE_001 - Unit not found
UNIDADE_002 - Invalid unit code
```

### 11. **CommonErrorMessage** (COMMON_XXX)
Erros genéricos do sistema
```
COMMON_001 - Database error
COMMON_002 - Configuration error
COMMON_003 - Internal server error
```

## Estrutura de Arquivos

```
src/main/java/com/api/erp/v1/main/shared/common/error/
├── ErrorMessage.java (mantém genéricos + template)
├── CommonErrorMessage.java
├── TenantErrorMessage.java
├── UserErrorMessage.java
├── ProductErrorMessage.java
├── CustomerErrorMessage.java
├── AddressErrorMessage.java
├── ContactErrorMessage.java
├── PermissionErrorMessage.java
├── FornecedorErrorMessage.java
├── CustomFieldErrorMessage.java
└── MeasureUnitErrorMessage.java
```

## Migration Path

### Fase 1: Templates
- Criar interface padrão para todos os ErrorMessages
- Manter ErrorMessage original como fallback

### Fase 2: Criação de Enums Específicas
- TenantErrorMessage.java
- UserErrorMessage.java
- ProductErrorMessage.java
- etc...

### Fase 3: Integração
- Atualizar serviços para usar enum específica
- Exemplo antes: `ErrorMessage.TENANT_NOT_EXISTS`
- Exemplo depois: `TenantErrorMessage.TENANT_NOT_FOUND`

### Fase 4: Limpeza
- Remover do ErrorMessage genérico
- Documentar padrão para novos features

## Vantagens

✅ Códigos únicos por domínio (TENANT_001 != USER_001)
✅ Fácil navegar por feature específica
✅ Melhor organização de erros relacionados
✅ Previne conflitos de código
✅ Escalável para novos features
✅ Melhor documentation por domínio
