package com.api.erp.v1.main.migration.domain.controller;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * DOMAIN - Interface de Controle da Fila Unificada de Migrações
 * 
 * Define os contratos e operações disponíveis para administração
 * da fila de processamento de migrações de tenants.
 * 
 * Responsabilidades:
 * - Fornecer visibilidade sobre eventos da fila
 * - Permitir reprocessamento de eventos falhados
 * - Monitorar progresso de migrações
 * - Listar eventos por status
 * 
 * @author ERP System
 * @version 1.0
 */
public interface IMigrationQueueAdminController {
    
    /**
     * Retorna estatísticas gerais da fila
     * 
     * Inclui:
     * - Total de eventos processados
     * - Eventos pendentes, em progresso, completados e falhados
     * - Taxa de sucesso
     * - Tempo médio de execução
     * 
     * @return Mapa com estatísticas da fila
     */
    ResponseEntity<Map<String, Object>> getQueueStats();
    
    /**
     * Lista todos os eventos da fila
     * 
     * @return Mapa com lista de todos os eventos
     */
    ResponseEntity<Map<String, Object>> getAllEvents();
    
    /**
     * Obtém detalhes de um evento específico
     * 
     * @param eventId ID único do evento
     * @return Mapa com detalhes do evento ou 404 se não encontrado
     */
    ResponseEntity<?> getEvent(String eventId);
    
    /**
     * Lista eventos pendentes (aguardando processamento)
     * 
     * @return Mapa com lista de eventos pendentes
     */
    ResponseEntity<Map<String, Object>> getPendingEvents();
    
    /**
     * Lista eventos que falharam
     * 
     * Útil para identificar problemas e planejar reprocessamento
     * 
     * @return Mapa com lista de eventos falhados
     */
    ResponseEntity<Map<String, Object>> getFailedEvents();
    
    /**
     * Lista eventos completados com sucesso
     * 
     * @return Mapa com lista de eventos concluídos
     */
    ResponseEntity<Map<String, Object>> getCompletedEvents();
    
    /**
     * Lista eventos em processamento
     * 
     * @return Mapa com lista de eventos em progresso
     */
    ResponseEntity<Map<String, Object>> getInProgressEvents();
    
    /**
     * Reprocessa um evento específico
     * 
     * Útil para reprocessar eventos que falharam após
     * resolver o problema raiz.
     * 
     * @param eventId ID único do evento a reprocessar
     * @return Mapa com resultado da operação
     */
    ResponseEntity<Map<String, Object>> reprocessEvent(String eventId);
}
