//package com.api.erp.v1.observability.application.aspect;
//
//import com.api.erp.v1.observability.application.annotation.TrackFlow;
//import com.api.erp.v1.observability.application.context.TraceContext;
//import com.api.erp.v1.observability.application.registry.StepRegistry;
//import com.api.erp.v1.observability.domain.FlowStatus;
//import com.api.erp.v1.observability.domain.FlowStep;
//import com.api.erp.v1.observability.domain.FlowTracker;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
///**
// * Exemplo de teste unitário para o FlowTrackingAspect.
// *
// * Testa:
// * - Emissão de evento START
// * - Emissão de evento SUCCESS com tempo
// * - Mappeamento de exceção para ERROR_*
// * - Medição de tempo
// */
//public class FlowTrackingAspectTest {
//
//    private FlowTracker mockTracker;
//    private StepRegistry stepRegistry;
//    private FlowTrackingAspect aspect;
//    private TestService service;
//
//    @BeforeEach
//    void setUp() {
//        mockTracker = mock(FlowTracker.class);
//        stepRegistry = new StepRegistry();
//        aspect = new FlowTrackingAspect(mockTracker, stepRegistry);
//
//        // Criar proxy do serviço de teste
//        TestService target = new TestService();
//        AspectJProxyFactory factory = new AspectJProxyFactory(target);
//        factory.addAspect(aspect);
//        service = factory.getProxy();
//
//        // Inicializar TraceContext
//        TraceContext.reset();
//    }
//
//    @Test
//    void testSuccessfulExecution() {
//        // Arrange
//        String traceId = TraceContext.getTraceId();
//
//        // Act
//        String result = service.successMethod("test");
//
//        // Assert
//        assertEquals("test-result", result);
//
//        // Verificar START foi chamado
//        verify(mockTracker).onStart(eq(traceId), any(FlowStep.class));
//
//        // Verificar SUCCESS foi chamado
//        ArgumentCaptor<String> traceIdCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<FlowStep> stepCaptor = ArgumentCaptor.forClass(FlowStep.class);
//        ArgumentCaptor<FlowStatus> statusCaptor = ArgumentCaptor.forClass(FlowStatus.class);
//        ArgumentCaptor<Integer> timeCaptor = ArgumentCaptor.forClass(Integer.class);
//
//        verify(mockTracker).onFinish(
//                traceIdCaptor.capture(),
//                stepCaptor.capture(),
//                statusCaptor.capture(),
//                timeCaptor.capture()
//        );
//
//        assertEquals(traceId, traceIdCaptor.getValue());
//        assertEquals("SUCCESS_METHOD", stepCaptor.getValue().getName());
//        assertEquals(FlowStatus.SUCCESS, statusCaptor.getValue());
//        assertTrue(timeCaptor.getValue() >= 0);
//    }
//
//    @Test
//    void testExceptionExecution() {
//        // Arrange
//        String traceId = TraceContext.getTraceId();
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> service.errorMethod());
//
//        // Verificar START foi chamado
//        verify(mockTracker).onStart(eq(traceId), any(FlowStep.class));
//
//        // Verificar ERROR foi chamado
//        ArgumentCaptor<String> traceIdCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<FlowStatus> statusCaptor = ArgumentCaptor.forClass(FlowStatus.class);
//
//        verify(mockTracker).onFinish(
//                traceIdCaptor.capture(),
//                any(FlowStep.class),
//                statusCaptor.capture(),
//                anyInt()
//        );
//
//        assertEquals(traceId, traceIdCaptor.getValue());
//        assertEquals(FlowStatus.ERROR_THROW, statusCaptor.getValue());
//    }
//
//    @Test
//    void testExecutionTimeIsPositive() {
//        // Arrange
//        service.slowMethod(100);
//
//        // Capture e verificar tempo
//        ArgumentCaptor<Integer> timeCaptor = ArgumentCaptor.forClass(Integer.class);
//
//        verify(mockTracker).onFinish(
//                anyString(),
//                any(FlowStep.class),
//                eq(FlowStatus.SUCCESS),
//                timeCaptor.capture()
//        );
//
//        assertTrue(timeCaptor.getValue() >= 100,
//                "Tempo de execução deve ser >= 100ms");
//    }
//
//    /**
//     * Classe de teste com métodos anotados.
//     */
//    public static class TestService {
//
//        @TrackFlow("SUCCESS_METHOD")
//        public String successMethod(String input) {
//            return input + "-result";
//        }
//
//        @TrackFlow("ERROR_METHOD")
//        public void errorMethod() {
//            throw new IllegalArgumentException("Erro proposital");
//        }
//
//        @TrackFlow("SLOW_METHOD")
//        public void slowMethod(long delayMs) {
//            try {
//                Thread.sleep(delayMs);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//}
