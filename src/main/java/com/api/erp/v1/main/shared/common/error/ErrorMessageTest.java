//package com.api.erp.v1.main.shared.common.error;
//
//import com.api.erp.v1.main.shared.domain.exception.BusinessException;
//import com.api.erp.v1.main.shared.domain.exception.NotFoundException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Tests for the ErrorMessage enum.
// *
// * Demonstrates practical usage of the enum methods and validations.
// */
//@DisplayName("ErrorMessage Enum Tests")
//public class ErrorMessageTest {
//
//    @Test
//    @DisplayName("Should return correct message for DATASOURCE_NOT_FOUND")
//    void testDatasourceNotFoundMessage() {
//        // Arrange & Act
//        String message = ErrorMessage.DATASOURCE_NOT_FOUND.getMessage();
//        String code = ErrorMessage.DATASOURCE_NOT_FOUND.getCode();
//        HttpStatus status = ErrorMessage.DATASOURCE_NOT_FOUND.getStatus();
//
//        // Assert
//        assertEquals("Datasource not found for the specified tenant.", message);
//        assertEquals("ERP_001", code);
//        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, status);
//    }
//
//    @Test
//    @DisplayName("Should return correct message for TENANT_NOT_EXISTS")
//    void testTenantNotExistsMessage() {
//        // Arrange & Act
//        String message = ErrorMessage.TENANT_NOT_EXISTS.getMessage();
//        String code = ErrorMessage.TENANT_NOT_EXISTS.getCode();
//        HttpStatus status = ErrorMessage.TENANT_NOT_EXISTS.getStatus();
//
//        // Assert
//        assertEquals("Tenant does not exist or is not active.", message);
//        assertEquals("ERP_002", code);
//        assertEquals(HttpStatus.NOT_FOUND, status);
//    }
//
//    @Test
//    @DisplayName("Should return correct message for DATABASE_ERROR")
//    void testDatabaseErrorMessage() {
//        // Arrange & Act
//        String message = ErrorMessage.DATABASE_ERROR.getMessage();
//        String code = ErrorMessage.DATABASE_ERROR.getCode();
//        HttpStatus status = ErrorMessage.DATABASE_ERROR.getStatus();
//
//        // Assert
//        assertEquals("Error accessing database. Please try again later.", message);
//        assertEquals("ERP_003", code);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);
//    }
//
//    @Test
//    @DisplayName("Should convert to BusinessException with correct HTTP status")
//    void testToBusinessException() {
//        // Arrange & Act
//        BusinessException exception = ErrorMessage.DATABASE_ERROR.toBusinessException();
//
//        // Assert
//        assertNotNull(exception);
//        assertEquals("Error accessing database. Please try again later.", exception.getMessage());
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
//    }
//
//    @Test
//    @DisplayName("Should convert to NotFoundException when NOT_FOUND")
//    void testToNotFoundExceptionSuccess() {
//        // Arrange & Act
//        NotFoundException exception = ErrorMessage.TENANT_NOT_EXISTS.toNotFoundException();
//
//        // Assert
//        assertNotNull(exception);
//        assertEquals("Tenant does not exist or is not active.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalStateException when converting non NOT_FOUND error")
//    void testToNotFoundExceptionFail() {
//        // Arrange & Act & Assert
//        assertThrows(IllegalStateException.class,
//            () -> ErrorMessage.DATABASE_ERROR.toNotFoundException()
//        );
//    }
//
//    @Test
//    @DisplayName("Should format toString correctly")
//    void testToString() {
//        // Arrange & Act
//        String result = ErrorMessage.DATASOURCE_NOT_FOUND.toString();
//
//        // Assert
//        assertEquals("[ERP_001] - Datasource not found for the specified tenant.", result);
//    }
//
//    @Test
//    @DisplayName("Should throw BusinessException via ErrorHandler")
//    void testErrorHandlerThrowBusinessException() {
//        // Arrange & Act & Assert
//        assertThrows(BusinessException.class,
//            () -> ErrorHandler.throwBusinessException(ErrorMessage.DATABASE_ERROR)
//        );
//    }
//
//    @Test
//    @DisplayName("Should throw NotFoundException via ErrorHandler")
//    void testErrorHandlerThrowNotFoundException() {
//        // Arrange & Act & Assert
//        assertThrows(NotFoundException.class,
//            () -> ErrorHandler.throwNotFoundException(ErrorMessage.TENANT_NOT_EXISTS)
//        );
//    }
//
//    @Test
//    @DisplayName("Should throw BusinessException with custom message")
//    void testErrorHandlerThrowWithCustomMessage() {
//        // Arrange
//        String customMessage = "Master database is unavailable";
//
//        // Act & Assert
//        try {
//            ErrorHandler.throwBusinessException(ErrorMessage.DATABASE_ERROR, customMessage);
//            fail("Should have thrown BusinessException");
//        } catch (BusinessException ex) {
//            assertEquals(customMessage, ex.getMessage());
//            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
//        }
//    }
//
//    @Test
//    @DisplayName("Should validate unique error codes")
//    void testUniqueErrorCodes() {
//        // Arrange & Act & Assert
//        long uniqueCodes = java.util.Arrays.stream(ErrorMessage.values())
//            .map(ErrorMessage::getCode)
//            .distinct()
//            .count();
//
//        assertEquals(ErrorMessage.values().length, uniqueCodes,
//            "All error codes must be unique");
//    }
//
//    @Test
//    @DisplayName("Should validate that all errors have message")
//    void testAllErrorsHaveMessage() {
//        // Arrange & Act & Assert
//        for (ErrorMessage error : ErrorMessage.values()) {
//            assertNotNull(error.getMessage(), "Error " + error + " does not have a message");
//            assertNotNull(error.getCode(), "Error " + error + " does not have a code");
//            assertNotNull(error.getStatus(), "Error " + error + " does not have an HTTP status");
//        }
//    }
//}
