# Error Handling System Analysis - ERP

## 📋 Implementation Summary

The complete error handling architecture of the ERP project was analyzed and a centralized `ENUM (ErrorMessage)` was implemented to manage generic error messages.

---

## 🏗️ Existing Error Architecture

### Exception Hierarchy

```
Throwable
├── Exception
│   └── RuntimeException
│       ├── BusinessException (Business Rules)
│       │   └── ProductException (Feature-specific)
│       │
│       ├── ValidationException (Validations)
│       │
│       ├── NotFoundException (Resource not found)
│       │
│       ├── DataSourceNotConfiguredException (Datasource not configured)
│       │
│       └── TenantDataSourceNotFoundException (Tenant not found)
```

### Main Components

| Component | Location | Responsibility |
|---|---|---|
| **BusinessException** | domain/exception | Base for business exceptions |
| **ValidationException** | domain/exception | Validations with field context |
| **NotFoundException** | domain/exception | Resources not found |
| **GlobalExceptionHandler** | infrastructure/exception | Intercepts and handles exceptions |
| **ErrorResponse** | infrastructure/exception | Standardized response DTO |
| **ErrorMessage** | shared/common/error | ✨ NEW - Generic error message enum |
| **ErrorHandler** | shared/common/error | ✨ NEW - Helper utility |

---

## ✨ New Implementation

### 1. **ErrorMessage ENUM**
   - **File**: `shared/common/error/ErrorMessage.java`
   - **Responsibility**: Centralize generic error messages
   - **Data per error**:
     - Unique code (ERP_001, ERP_002, etc)
     - Descriptive message in English
     - Appropriate HTTP status

   **Available enums**:
   ```
   ✓ DATASOURCE_NOT_FOUND (ERP_001) → 503 Service Unavailable
   ✓ TENANT_NOT_EXISTS (ERP_002) → 404 Not Found
   ✓ DATABASE_ERROR (ERP_003) → 500 Internal Server Error
   ✓ DATABASE_CONNECTION_FAILED (ERP_004) → 503 Service Unavailable
   ✓ CONFIGURATION_ERROR (ERP_005) → 500 Internal Server Error
   ✓ INTERNAL_SERVER_ERROR (ERP_999) → 500 Internal Server Error
   ```

### 2. **ErrorHandler Utility Class**
   - **File**: `shared/common/error/ErrorHandler.java`
   - **Responsibility**: Facilitate ErrorMessage usage
   - **Main methods**:
     - `throwBusinessException()` - Throws with HTTP status
     - `throwNotFoundException()` - For resource not found
     - `logAndThrow()` - Log + exception
     - `executeWithDatabaseErrorHandling()` - Execute with error handling

### 3. **Unit Tests**
   - **File**: `shared/common/error/ErrorMessageTest.java`
   - **Coverage**: 
     - Message validation
     - Exception conversion
     - String formatting
     - Code uniqueness

### 4. **Documentation**
   - **File**: `shared/common/error/ERROR_MESSAGE_GUIDE.md`
   - **Content**:
     - Enum overview
     - Available errors table
     - Practical usage examples
     - GlobalExceptionHandler integration
     - Best practices

---

## 🔄 Error Handling Flow

### Scenario 1: Tenant Not Found

```
1. Service/Controller validates tenant
   ↓
2. throw ErrorHandler.throwNotFoundException(ErrorMessage.TENANT_NOT_EXISTS)
   ↓
3. Throws NotFoundException with enum message
   ↓
4. GlobalExceptionHandler.handleNotFoundException() intercepts
   ↓
5. Returns ErrorResponse with:
   - status: 404
   - error: "Not Found"
   - message: "Tenant does not exist or is not active."
   - timestamp: ISO 8601
```

### Scenario 2: Database Error

```
1. Database operation fails
   ↓
2. ErrorHandler.executeWithDatabaseErrorHandling(action, logger)
   ↓
3. Catches exception and logs
   ↓
4. Throws BusinessException with ErrorMessage.DATABASE_ERROR
   ↓
5. GlobalExceptionHandler.handleBusinessException() intercepts
   ↓
6. Returns ErrorResponse with:
   - status: 500
   - error: "Internal Server Error"
   - message: "Error accessing database. Please try again later."
```

---

## 📊 Usage Examples

### Example 1: In a Service

```java
@Service
public class TenantService {
    private static final Logger log = LoggerFactory.getLogger(TenantService.class);
    
    public TenantDTO getTenant(Long tenantId) {
        // Validate existence
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> ErrorMessage.TENANT_NOT_EXISTS.toNotFoundException());
        
        // Validate datasource
        if (!isDatasourceConfigured(tenant)) {
            throw ErrorHandler.throwBusinessException(
                ErrorMessage.DATASOURCE_NOT_FOUND,
                "Tenant ID: " + tenantId
            );
        }
        
        return tenantMapper.toDTO(tenant);
    }
}
```

### Example 2: With Logging

```java
public class CustomerRepository {
    private static final Logger log = LoggerFactory.getLogger(CustomerRepository.class);
    
    public Customer findById(Long id) {
        return ErrorHandler.executeWithDatabaseErrorHandling(
            () -> entityManager
                .createQuery("SELECT c FROM Customer c WHERE c.id = :id")
                .setParameter("id", id)
                .getSingleResult(),
            log
        );
    }
}
```

### Example 3: In a Controller

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        try {
            return productService.findById(id);
        } catch (DataAccessException e) {
            throw ErrorHandler.throwBusinessException(
                ErrorMessage.DATABASE_ERROR,
                "Error fetching product: " + id
            );
        }
    }
}
```

---

## 📁 Created File Structure

```
src/main/java/com/api/erp/v1/main/shared/common/error/
├── ErrorMessage.java                 (Centralized enum)
├── ErrorHandler.java                 (Helper utility)
├── ErrorMessageTest.java             (Unit tests)
└── ERROR_MESSAGE_GUIDE.md            (Documentation)
```

---

## ✅ Implementation Checklist

- [x] Centralized enum with 6 error types
- [x] Appropriate HTTP status for each error
- [x] BusinessException integration
- [x] NotFoundException integration
- [x] ErrorHandler utility class with helper methods
- [x] Automatic logging support
- [x] Complete unit tests
- [x] Documentation with examples
- [x] Unique code validation
- [x] Exception conversion
- [x] Customizable messages
- [x] English language support

---

## 🎯 Recommended Next Steps

1. **Gradual Migration**
   - Update existing services to use ErrorMessage
   - Start with datasource and tenant services

2. **Expand Errors**
   - Add new errors as needed
   - Keep enum in shared.common.error

3. **Observability Integration**
   - Map ErrorMessage codes in `BusinessErrorMappingStrategy`
   - Add observability for each error type

4. **Frontend**
   - Consume error codes (ERP_001, ERP_002, etc)
   - Map messages for end user

---

## 🔗 Related

- [GlobalExceptionHandler](../../infrastructure/exception/GlobalExceptionHandler.java)
- [BusinessException](../../domain/exception/BusinessException.java)
- [NotFoundException](../../domain/exception/NotFoundException.java)
- [ErrorResponse](../../infrastructure/exception/ErrorResponse.java)
- [BusinessErrorMappingStrategy](../../../../external/observability/strategy/BusinessErrorMappingStrategy.java)

---

## 📝 Notes

- The system already has GlobalExceptionHandler that automatically intercepts BusinessException and NotFoundException
- ErrorMessage enum works transparently with existing handlers
- No need to add new handlers to GlobalExceptionHandler
- Error codes follow the ERP_XXX pattern for easy identification

---

**Date**: 02/22/2026  
**Version**: 1.0  
**Language**: English  
**Status**: ✅ Implemented and Documented
