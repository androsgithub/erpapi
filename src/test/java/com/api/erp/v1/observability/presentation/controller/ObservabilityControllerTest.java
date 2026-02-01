package com.api.erp.v1.observability.presentation.controller;

import com.api.erp.v1.observability.presentation.dto.FlowEventDto;
import com.api.erp.v1.observability.presentation.dto.ObservabilityStatsDto;
import com.api.erp.v1.observability.application.service.ObservabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para ObservabilityController
 * 
 * Demonstra como testar os endpoints do controller.
 * Serve como referência para testar a integração com a API REST.
 */
@WebMvcTest(ObservabilityController.class)
@DisplayName("ObservabilityController - Testes dos Endpoints")
class ObservabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObservabilityService observabilityService;

    private FlowEventDto sampleEvent;
    private ObservabilityStatsDto sampleStats;

    @BeforeEach
    void setUp() {
        // Preparar dados de exemplo
        sampleEvent = new FlowEventDto(
            1L,
            "trace-123",
            "validateUser",
            "SUCCESS",
            150,
            Instant.now()
        );

        sampleStats = new ObservabilityStatsDto(
            100L,
            5L,
            95,
            Map.of(
                "ValidationError", 3L,
                "DatabaseError", 2L
            ),
            125,
            List.of(sampleEvent)
        );
    }

    @Test
    @DisplayName("Deve retornar eventos por traceId")
    void testGetEventsByTraceId() throws Exception {
        // Arrange
        String traceId = "trace-123";
        when(observabilityService.findEventsByTraceId(traceId))
            .thenReturn(List.of(sampleEvent));

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/trace/{traceId}", traceId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].traceId").value("trace-123"))
            .andExpect(jsonPath("$[0].stepName").value("validateUser"))
            .andExpect(jsonPath("$[0].status").value("SUCCESS"));

        verify(observabilityService, times(1)).findEventsByTraceId(traceId);
    }

    @Test
    @DisplayName("Deve retornar traceId vazio com lista vazia")
    void testGetEventsByTraceId_NotFound() throws Exception {
        // Arrange
        String traceId = "trace-unknown";
        when(observabilityService.findEventsByTraceId(traceId))
            .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/trace/{traceId}", traceId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(observabilityService, times(1)).findEventsByTraceId(traceId);
    }

    @Test
    @DisplayName("Deve retornar estatísticas gerais")
    void testGetStats() throws Exception {
        // Arrange
        when(observabilityService.getStats(24))
            .thenReturn(sampleStats);

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/stats")
            .param("hours", "24"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalEvents").value(100))
            .andExpect(jsonPath("$.totalErrors").value(5))
            .andExpect(jsonPath("$.successRate").value(95))
            .andExpect(jsonPath("$.averageExecutionTimeMs").value(125));

        verify(observabilityService, times(1)).getStats(24);
    }

    @Test
    @DisplayName("Deve usar valor padrão de 24 horas para stats")
    void testGetStats_DefaultHours() throws Exception {
        // Arrange
        when(observabilityService.getStats(24))
            .thenReturn(sampleStats);

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalEvents").value(100));

        verify(observabilityService, times(1)).getStats(24);
    }

    @Test
    @DisplayName("Deve retornar eventos recentes")
    void testGetRecentEvents() throws Exception {
        // Arrange
        when(observabilityService.getRecentEvents(50))
            .thenReturn(List.of(sampleEvent));

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/recent")
            .param("limit", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stepName").value("validateUser"));

        verify(observabilityService, times(1)).getRecentEvents(50);
    }

    @Test
    @DisplayName("Deve retornar erros recentes")
    void testGetRecentErrors() throws Exception {
        // Arrange
        FlowEventDto errorEvent = new FlowEventDto(
            2L, "trace-456", "processPayment", "ERROR", 500, Instant.now()
        );
        when(observabilityService.getRecentErrors(24))
            .thenReturn(List.of(errorEvent));

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/errors")
            .param("hours", "24"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("ERROR"));

        verify(observabilityService, times(1)).getRecentErrors(24);
    }

    @Test
    @DisplayName("Deve retornar eventos de um período")
    void testGetEventsBetween() throws Exception {
        // Arrange
        when(observabilityService.findEventsBetween(24, 0))
            .thenReturn(List.of(sampleEvent));

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/period")
            .param("startHours", "24")
            .param("endHours", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stepName").value("validateUser"));

        verify(observabilityService, times(1)).findEventsBetween(24, 0);
    }

    @Test
    @DisplayName("Deve retornar erros de um período")
    void testGetErrorsBetween() throws Exception {
        // Arrange
        FlowEventDto errorEvent = new FlowEventDto(
            2L, "trace-456", "processPayment", "ERROR", 500, Instant.now()
        );
        when(observabilityService.findErrorsBetween(24, 0))
            .thenReturn(List.of(errorEvent));

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/errors/period")
            .param("startHours", "24")
            .param("endHours", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));

        verify(observabilityService, times(1)).findErrorsBetween(24, 0);
    }

    @Test
    @DisplayName("Deve retornar status de saúde")
    void testHealth() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve validar período válido (startHours > endHours)")
    void testGetEventsBetween_ValidPeriod() throws Exception {
        // Arrange
        when(observabilityService.findEventsBetween(48, 24))
            .thenReturn(List.of(sampleEvent));

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/period")
            .param("startHours", "48")
            .param("endHours", "24"))
            .andExpect(status().isOk());

        verify(observabilityService, times(1)).findEventsBetween(48, 24);
    }

    @Test
    @DisplayName("Deve aceitar limite personalizado de eventos")
    void testGetRecentEvents_CustomLimit() throws Exception {
        // Arrange
        when(observabilityService.getRecentEvents(100))
            .thenReturn(List.of(sampleEvent));

        // Act & Assert
        mockMvc.perform(get("/api/v1/observability/events/recent")
            .param("limit", "100"))
            .andExpect(status().isOk());

        verify(observabilityService, times(1)).getRecentEvents(100);
    }
}
