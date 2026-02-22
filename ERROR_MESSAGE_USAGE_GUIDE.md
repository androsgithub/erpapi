# ErrorMessage Usage Guide

## Overview
O sistema agora possui **ErrorMessages específicas por domínio** para melhor organização, evitar conflitos de código e facilitar manutenção.

## Estrutura

### 1. Interface Padrão
```java
public interface IErrorMessage {
    String getMessage();
    String getCode();
    HttpStatus getStatus();
    BusinessException toBusinessException();
    NotFoundException toNotFoundException();
}
```

### 2. ErrorMessages Disponíveis

| Domain | Enum | Code Range | Exemplos |
|--------|------|------------|----------|
| Tenant | `TenantErrorMessage` | TENANT_001-099 | TENANT_NOT_FOUND, DATASOURCE_NOT_CONFIGURED |
| User | `UserErrorMessage` | USER_001-099 | USER_NOT_FOUND, EMAIL_ALREADY_REGISTERED |
| Product | `ProductErrorMessage` | PRODUCT_001-099 | PRODUCT_NOT_FOUND, CIRCULAR_COMPOSITION |
| Customer | `CustomerErrorMessage` | CUSTOMER_001-099 | CUSTOMER_NOT_FOUND, INVALID_CUSTOMER_DATA |
| Address | `AddressErrorMessage` | ADDRESS_001-099 | ADDRESS_NOT_FOUND, INVALID_POSTAL_CODE |
| Contact | `ContactErrorMessage` | CONTACT_001-099 | CONTACT_NOT_FOUND, INVALID_CONTACT_TYPE |
| Permission | `PermissionErrorMessage` | PERMISSION_001-099 | ACCESS_DENIED, PERMISSION_NOT_FOUND |
| Fornecedor | `FornecedorErrorMessage` | FORNECEDOR_001-099 | SUPPLIER_NOT_FOUND, SUPPLIER_ALREADY_REGISTERED |
| CustomField | `CustomFieldErrorMessage` | CUSTOMFIELD_001-099 | FIELD_NOT_FOUND, FIELD_ALREADY_REGISTERED |
| Unidade Medida | `MeasureUnitErrorMessage` | UNIDADE_001-099 | UNIT_NOT_FOUND, INVALID_UNIT_CODE |
| Common | `CommonErrorMessage` | COMMON_001-099 | DATABASE_ERROR, CONFIGURATION_ERROR |

## Usage Examples

### 1. Throwing Business Exceptions

```java
// Before (generic)
throw new BusinessException("Tenant not found");

// After (with ErrorMessage)
throw TenantErrorMessage.TENANT_NOT_FOUND.toBusinessException();
```

### 2. Throwing NotFound Exceptions

```java
// Serviço:
Tenant tenant = tenantRepository.findById(id)
    .orElseThrow(() -> TenantErrorMessage.TENANT_NOT_FOUND.toNotFoundException());

// User:
User user = userRepository.findById(id)
    .orElseThrow(() -> UserErrorMessage.USER_NOT_FOUND.toNotFoundException());

// Product:
Product product = productRepository.findById(id)
    .orElseThrow(() -> ProductErrorMessage.PRODUCT_NOT_FOUND.toNotFoundException());
```

### 3. Using with ErrorHandler

```java
@Override
public void deletarDatasource(Long tenantId) {
    try {
        // ... operações ...
        datasourceRepository.delete(datasource);
    } catch (Exception e) {
        ErrorHandler.logAndThrow(
            log,
            TenantErrorMessage.DATASOURCE_CONNECTION_FAILED,
            "Failed to delete datasource for tenant: " + tenantId
        );
    }
}
```

### 4. In Validators

```java
@Component
public class TenantValidator {
    public void validarTenant(Tenant tenant) {
        if (tenant == null) {
            throw TenantErrorMessage.TENANT_NOT_FOUND.toBusinessException();
        }
        
        if (tenant.getDatasource() == null) {
            throw TenantErrorMessage.DATASOURCE_NOT_CONFIGURED.toBusinessException();
        }
    }
}
```

### 5. Getting Error Information

```java
// Obter mensagem
String message = TenantErrorMessage.TENANT_NOT_FOUND.getMessage();
// "Tenant not found or is inactive."

// Obter código
String code = TenantErrorMessage.TENANT_NOT_FOUND.getCode();
// "TENANT_001"

// Obter status HTTP
HttpStatus status = TenantErrorMessage.TENANT_NOT_FOUND.getStatus();
// HttpStatus.NOT_FOUND (404)

// String formatada
String formatted = TenantErrorMessage.TENANT_NOT_FOUND.toString();
// "[TENANT_001] - Tenant not found or is inactive."
```

## Creating New ErrorMessages

### Step 1: Identify Domain
```
Feature name → Domain code
Product Feature → PRODUCT (3-letter prefix)
```

### Step 2: Create Enum
```java
// src/main/java/.../shared/common/error/DomainErrorMessage.java
public enum DomainErrorMessage implements IErrorMessage {
    ERROR_NAME(
        "Clear error description",
        "DOMAIN_001",
        HttpStatus.APPROPRIATE_STATUS
    );
    
    // ... implementation (copy from existing enums) ...
}
```

### Step 3: Add to Domain Service
```java
@Service
public class DomainService {
    public void operate(Long id) {
        Entity entity = repository.findById(id)
            .orElseThrow(() -> DomainErrorMessage.ENTITY_NOT_FOUND.toNotFoundException());
    }
}
```

## Code Registry

Keep track of used codes to prevent conflicts:

- **TENANT_001 to TENANT_008**: Disponíveis até TENANT_099
- **USER_001 to USER_012**: Disponíveis até USER_099
- **PRODUCT_001 to PRODUCT_010**: Disponíveis até PRODUCT_099
- **CUSTOMER_001 to CUSTOMER_003**: Disponíveis até CUSTOMER_099
- **ADDRESS_001 to ADDRESS_003**: Disponíveis até ADDRESS_099
- **CONTACT_001 to CONTACT_004**: Disponíveis até CONTACT_099
- **PERMISSION_001 to PERMISSION_003**: Disponíveis até PERMISSION_099
- **FORNECEDOR_001 to FORNECEDOR_003**: Disponíveis até FORNECEDOR_099
- **CUSTOMFIELD_001 to CUSTOMFIELD_006**: Disponíveis até CUSTOMFIELD_099
- **UNIDADE_001 to UNIDADE_004**: Disponíveis até UNIDADE_099
- **COMMON_001 to COMMON_006, COMMON_999**: Disponíveis até COMMON_099

## Best Practices

✅ **DO:**
- Use domain-specific ErrorMessage enums
- Include context in error messages via ErrorHandler.logAndThrow()
- Use `.toNotFoundException()` for 404 errors
- Use `.toBusinessException()` for other HTTP statuses
- Document error codes for each feature

❌ **DON'T:**
- Hardcode error messages in services
- Use generic ErrorMessage for domain-specific errors
- Mix error codes between domains
- Forget to implement IErrorMessage in new enums
- Leave error codes undefined in domain enums

## Deprecated

⚠️ The generic `ErrorMessage` enum is now deprecated:
- DATASOURCE_NOT_FOUND → Use `TenantErrorMessage.DATASOURCE_NOT_CONFIGURED`
- TENANT_NOT_EXISTS → Use `TenantErrorMessage.TENANT_NOT_FOUND`
- DATABASE_* → Use `CommonErrorMessage.DATABASE_*`

Migrate old code gradually to feature-specific enums.
