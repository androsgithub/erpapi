package com.api.erp.v1.observability.presentation.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para PageableRequest e PageableResponse
 */
@DisplayName("Paginação - Testes de Request e Response")
class PaginationTest {

    @Test
    @DisplayName("Deve criar PageableRequest com valores padrão")
    void testPageableRequestDefaults() {
        PageableRequest request = new PageableRequest();
        
        assertEquals(0, request.getPage());
        assertEquals(20, request.getPageSize());
        assertEquals("timestamp", request.getSortBy());
        assertEquals("DESC", request.getSortDirection());
        assertTrue(request.getFilters().isEmpty());
    }

    @Test
    @DisplayName("Deve validar pageSize máximo de 100")
    void testPageableSizeLimit() {
        PageableRequest request = new PageableRequest();
        request.setPageSize(500);
        
        assertEquals(100, request.getPageSize());
    }

    @Test
    @DisplayName("Deve calcular offset corretamente")
    void testPageableOffset() {
        PageableRequest request = new PageableRequest(2, 20);
        
        assertEquals(40, request.getOffset());
    }

    @Test
    @DisplayName("Deve validar page não negativo")
    void testPageableNegativePage() {
        PageableRequest request = new PageableRequest();
        request.setPage(-5);
        
        assertEquals(0, request.getPage());
    }

    @Test
    @DisplayName("Deve adicionar e remover filtros")
    void testPageableFilters() {
        PageableRequest request = new PageableRequest();
        
        request.addFilter("status", "ERROR");
        request.addFilter("stepName", "validateUser");
        
        assertEquals(2, request.getFilters().size());
        assertEquals("ERROR", request.getFilters().get("status"));
        
        request.removeFilter("status");
        assertEquals(1, request.getFilters().size());
        assertNull(request.getFilters().get("status"));
    }

    @Test
    @DisplayName("Deve validar sortDirection")
    void testPageableSortDirection() {
        PageableRequest request = new PageableRequest();
        
        request.setSortDirection("ASC");
        assertEquals("ASC", request.getSortDirection());
        
        request.setSortDirection("INVALID");
        assertEquals("DESC", request.getSortDirection());
    }

    @Test
    @DisplayName("Deve verificar se tem query")
    void testPageableHasQuery() {
        PageableRequest request = new PageableRequest();
        
        assertFalse(request.hasQuery());
        
        request.setQuery("validateUser");
        assertTrue(request.hasQuery());
        
        request.setQuery("   ");
        assertFalse(request.hasQuery());
    }

    @Test
    @DisplayName("Deve verificar se tem filtros")
    void testPageableHasFilters() {
        PageableRequest request = new PageableRequest();
        
        assertFalse(request.hasFilters());
        
        request.addFilter("status", "ERROR");
        assertTrue(request.hasFilters());
    }

    @Test
    @DisplayName("Deve criar PageableResponse com dados")
    void testPageableResponseCreation() {
        var data = java.util.List.of("item1", "item2", "item3");
        
        PageableResponse<String> response = PageableResponse.of(data, 0, 20, 50);
        
        assertEquals(3, response.getData().size());
        assertEquals(0, response.getPage());
        assertEquals(20, response.getPageSize());
        assertEquals(50, response.getTotalElements());
        assertEquals(3, response.getTotalPages()); // ceil(50/20)
    }

    @Test
    @DisplayName("Deve calcular hasNext corretamente")
    void testPageableResponseHasNext() {
        var data = java.util.List.of("item1", "item2");
        
        // Página 0 de 2 - tem próxima
        PageableResponse<String> response1 = PageableResponse.of(data, 0, 20, 50);
        assertTrue(response1.isHasNext());
        
        // Página 2 de 3 - tem próxima
        PageableResponse<String> response2 = PageableResponse.of(data, 2, 20, 60);
        assertFalse(response2.isHasNext()); // É última página
    }

    @Test
    @DisplayName("Deve calcular hasPrevious corretamente")
    void testPageableResponseHasPrevious() {
        var data = java.util.List.of("item1", "item2");
        
        // Página 0 - sem anterior
        PageableResponse<String> response1 = PageableResponse.of(data, 0, 20, 50);
        assertFalse(response1.isHasPrevious());
        
        // Página 2 - com anterior
        PageableResponse<String> response2 = PageableResponse.of(data, 2, 20, 60);
        assertTrue(response2.isHasPrevious());
    }

    @Test
    @DisplayName("Deve identificar primeira e última página")
    void testPageableResponseFirstLast() {
        var data = java.util.List.of("item1");
        
        // Primeira página
        PageableResponse<String> first = PageableResponse.of(data, 0, 20, 50);
        assertTrue(first.isFirst());
        assertFalse(first.isLast());
        
        // Última página
        PageableResponse<String> last = PageableResponse.of(data, 2, 20, 50);
        assertFalse(last.isFirst());
        assertTrue(last.isLast());
    }

    @Test
    @DisplayName("Deve criar resposta vazia")
    void testPageableResponseEmpty() {
        PageableResponse<String> response = PageableResponse.empty(0, 20);
        
        assertEquals(0, response.getData().size());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertFalse(response.isHasNext());
    }

    @Test
    @DisplayName("Deve criar ObservabilityFilter por status")
    void testObservabilityFilterByStatus() {
        ObservabilityFilter filter = ObservabilityFilter.byStatus("ERROR");
        
        assertEquals("ERROR", filter.getStatus());
        assertTrue(filter.hasStatus());
        assertTrue(filter.hasAnyFilter());
    }

    @Test
    @DisplayName("Deve criar ObservabilityFilter por step")
    void testObservabilityFilterByStep() {
        ObservabilityFilter filter = ObservabilityFilter.byStep("validateUser");
        
        assertEquals("validateUser", filter.getStepName());
        assertTrue(filter.hasStepName());
        assertTrue(filter.hasAnyFilter());
    }

    @Test
    @DisplayName("Deve criar ObservabilityFilter apenas erros")
    void testObservabilityFilterErrorsOnly() {
        ObservabilityFilter filter = ObservabilityFilter.errorsOnly();
        
        assertTrue(filter.getErrorsOnly());
        assertTrue(filter.hasAnyFilter());
    }

    @Test
    @DisplayName("Deve validar range de execução")
    void testObservabilityFilterExecutionTime() {
        ObservabilityFilter filter = ObservabilityFilter.byExecutionTime(100, 500);
        
        assertEquals(100, filter.getMinExecutionTime());
        assertEquals(500, filter.getMaxExecutionTime());
        assertTrue(filter.hasExecutionTimeRange());
    }

    @Test
    @DisplayName("Deve validar período")
    void testObservabilityFilterPeriod() {
        ObservabilityFilter filter = ObservabilityFilter.byPeriod(24, 0);
        
        assertEquals(24, filter.getStartHours());
        assertEquals(0, filter.getEndHours());
    }

    @Test
    @DisplayName("Deve usar defaults de período")
    void testObservabilityFilterPeriodDefaults() {
        ObservabilityFilter filter = new ObservabilityFilter();
        
        assertEquals(24, filter.getStartHours());
        assertEquals(0, filter.getEndHours());
    }

    @Test
    @DisplayName("Deve criar SearchBuilder fluente")
    void testSearchBuilder() {
        var builder = SearchBuilder.create()
            .withStatus("ERROR")
            .withStepName("validateUser")
            .withMinExecutionTime(100)
            .withPage(0, 20);
        
        assertEquals(0, builder.getPage());
        assertEquals(20, builder.getPageSize());
        assertTrue(builder.hasFilters());
    }

    @Test
    @DisplayName("Deve calcular offset correto em SearchBuilder")
    void testSearchBuilderOffset() {
        var builder = SearchBuilder.create()
            .withPage(5, 20);
        
        assertEquals(100, builder.getOffset());
    }

    @Test
    @DisplayName("Deve retornar Sort do Spring")
    void testSearchBuilderSort() {
        var builder = SearchBuilder.create()
            .withSort("timestamp", "DESC");
        
        assertNotNull(builder.getSort());
    }

    @Test
    @DisplayName("Deve calcular Instant para período")
    void testSearchBuilderInstant() {
        var builder = SearchBuilder.create()
            .withPeriod(24, 0);
        
        var startInstant = builder.getStartInstant();
        var endInstant = builder.getEndInstant();
        
        assertNotNull(startInstant);
        assertNotNull(endInstant);
        assertTrue(startInstant.isBefore(endInstant));
    }

    @Test
    @DisplayName("Deve validar que pageSize não pode ser 0")
    void testPageableSizeMinimum() {
        PageableRequest request = new PageableRequest();
        request.setPageSize(0);
        
        assertEquals(1, request.getPageSize());
    }

    @Test
    @DisplayName("Deve validar PageableRequest")
    void testPageableRequestValidation() {
        PageableRequest request = new PageableRequest();
        
        assertTrue(request.isValid());
        
        request.setPage(-1);
        assertFalse(request.isValid());
        
        request.setPage(0);
        request.setPageSize(-5);
        assertFalse(request.isValid());
    }
}
