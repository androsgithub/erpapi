package com.api.erp.v1.observability.domain.repository;

import com.api.erp.v1.observability.domain.entity.FlowEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FlowEventRepository extends JpaRepository<FlowEventEntity, Long> {

    List<FlowEventEntity> findByTraceIdOrderByTimestamp(String traceId);

    List<FlowEventEntity> findByStepIdOrderByTimestamp(Integer stepId);

    @Query("SELECT e FROM FlowEventEntity e WHERE e.timestamp BETWEEN :start AND :end ORDER BY e.timestamp DESC")
    List<FlowEventEntity> findEventsBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT e.status, COUNT(e) FROM FlowEventEntity e GROUP BY e.status")
    List<Object[]> countByStatus();

    @Query("SELECT e FROM FlowEventEntity e WHERE e.status >= 3 AND e.timestamp BETWEEN :start AND :end ORDER BY e.timestamp DESC")
    List<FlowEventEntity> findErrorsBetween(@Param("start") Instant start, @Param("end") Instant end);
}
