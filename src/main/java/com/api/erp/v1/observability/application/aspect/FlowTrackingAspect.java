package com.api.erp.v1.observability.application.aspect;

import com.api.erp.v1.observability.application.annotation.TrackFlow;
import com.api.erp.v1.observability.application.context.TraceContext;
import com.api.erp.v1.observability.application.mapper.FlowErrorMapper;
import com.api.erp.v1.observability.application.registry.StepRegistry;
import com.api.erp.v1.observability.domain.FlowStep;
import com.api.erp.v1.observability.domain.FlowStatus;
import com.api.erp.v1.observability.domain.FlowTracker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspecto responsável pela instrumentação automática de métodos anotados com @TrackFlow.
 * Intercepta execução e emite eventos de início e término.
 * 
 * Responsabilidades:
 * - Obter traceId do contexto
 * - Registrar step
 * - Medir tempo de execução
 * - Mapear exceções para status
 * - Delegar emissão de eventos ao tracker (sem lógica de negócio)
 */
@Aspect
@Component
public class FlowTrackingAspect {
    private final FlowTracker flowTracker;
    private final StepRegistry stepRegistry;

    public FlowTrackingAspect(FlowTracker flowTracker, StepRegistry stepRegistry) {
        this.flowTracker = flowTracker;
        this.stepRegistry = stepRegistry;
    }

    @Around("@annotation(trackFlow)")
    public Object trackFlowExecution(ProceedingJoinPoint joinPoint, TrackFlow trackFlow) throws Throwable {
        String stepName = trackFlow.value();
        FlowStep step = stepRegistry.getOrRegisterStep(stepName);
        String traceId = TraceContext.getTraceId();

        // Emite evento START
        flowTracker.onStart(traceId, step);

        long startNanoTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();

            long endNanoTime = System.nanoTime();
            int executionTimeMs = convertToMilliseconds(startNanoTime, endNanoTime);

            // Emite evento SUCCESS
            flowTracker.onFinish(traceId, step, FlowStatus.SUCCESS, executionTimeMs);

            return result;

        } catch (Throwable exception) {
            long endNanoTime = System.nanoTime();
            int executionTimeMs = convertToMilliseconds(startNanoTime, endNanoTime);

            FlowStatus status = FlowErrorMapper.mapException(exception);

            // Emite evento ERROR_*
            flowTracker.onFinish(traceId, step, status, executionTimeMs);

            // Relança a exceção para que o método falhe normalmente
            throw exception;
        }
    }

    /**
     * Converte tempo de nano para milissegundos.
     */
    private int convertToMilliseconds(long startNanoTime, long endNanoTime) {
        long elapsedNanos = endNanoTime - startNanoTime;
        return (int) (elapsedNanos / 1_000_000);
    }
}
