package com.api.erp.v1.main.datasource.routing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

/**
 * TESTES UNITÁRIOS - TenantContext
 * 
 * Cenários cobertos:
 * 1. Definição e recuperação de tenant ID
 * 2. Definição e recuperação de group ID
 * 3. Definição e recuperação de group IDs (lista)
 * 4. Limpeza de contexto
 * 5. Isolamento de contexto em cenários multi-thread (ThreadLocal)
 * 6. Comportamento com valores null
 * 
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantContext - Testes Unitários")
class TenantContextTest {

    @BeforeEach
    void setUp() {
        // Limpar contexto antes de cada teste
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        // Limpar contexto após cada teste para evitar contaminação
        TenantContext.clear();
    }

    // ===== Testes de Tenant ID =====

    @Test
    @DisplayName("dado_tenantId_quando_setTenantId_entao_getTenantIdRetornaValorDefinido")
    void testGivenTenantId_WhenSetTenantId_ThenGetTenantIdReturnsValue() {
        // Arrange
        Long expectedTenantId = 1L;

        // Act
        TenantContext.setTenantId(expectedTenantId);
        Long actualTenantId = TenantContext.getTenantId();

        // Assert
        assertThat(actualTenantId)
                .as("Tenant ID deve ser retornado corretamente após definição")
                .isEqualTo(expectedTenantId);
    }

    @Test
    @DisplayName("dado_multiplosTenantIds_quando_setTenantIdSequencialmente_entao_ultimoValorEhRetornado")
    void testGivenMultipleTenantIds_WhenSetTenantIdSequentially_ThenLastValueReturned() {
        // Arrange
        Long tenantId1 = 1L;
        Long tenantId2 = 2L;
        Long expectedTenantId = 2L;

        // Act
        TenantContext.setTenantId(tenantId1);
        TenantContext.setTenantId(tenantId2);
        Long actualTenantId = TenantContext.getTenantId();

        // Assert - último valor definido sobrescreve o anterior
        assertThat(actualTenantId)
                .as("Último tenant ID definido deve ser retornado")
                .isEqualTo(expectedTenantId);
    }

    @Test
    @DisplayName("dado_nenhunTenantIdDefinido_quando_getTenantId_entao_retornaNull")
    void testGivenNoTenantIdDefined_WhenGetTenantId_ThenReturnsNull() {
        // Act
        Long tenantId = TenantContext.getTenantId();

        // Assert
        assertThat(tenantId)
                .as("getTenantId deve retornar null quando nenhum valor foi definido")
                .isNull();
    }

    @Test
    @DisplayName("dado_tenantIdDefinido_quando_clear_entao_getTenantIdRetornaNull")
    void testGivenTenantIdDefined_WhenClear_ThenGetTenantIdReturnsNull() {
        // Arrange
        TenantContext.setTenantId(1L);

        // Act
        TenantContext.clear();
        Long tenantId = TenantContext.getTenantId();

        // Assert
        assertThat(tenantId)
                .as("Tenant ID deve ser null após limpeza")
                .isNull();
    }

    // ===== Testes de Group ID =====

    @Test
    @DisplayName("dado_groupId_quando_setGroupId_entao_getGroupIdRetornaValorDefinido")
    void testGivenGroupId_WhenSetGroupId_ThenGetGroupIdReturnsValue() {
        // Arrange
        Long expectedGroupId = 10L;

        // Act
        TenantContext.setGroupId(expectedGroupId);
        Long actualGroupId = TenantContext.getGroupId();

        // Assert
        assertThat(actualGroupId)
                .as("Group ID deve ser retornado corretamente após definição")
                .isEqualTo(expectedGroupId);
    }

    @Test
    @DisplayName("dado_nenhunGroupIdDefinido_quando_getGroupId_entao_retornaNull")
    void testGivenNoGroupIdDefined_WhenGetGroupId_ThenReturnsNull() {
        // Act
        Long groupId = TenantContext.getGroupId();

        // Assert
        assertThat(groupId)
                .as("getGroupId deve retornar null quando nenhum valor foi definido")
                .isNull();
    }

    @Test
    @DisplayName("dado_groupIdDefinido_quando_clear_entao_getGroupIdRetornaNull")
    void testGivenGroupIdDefined_WhenClear_ThenGetGroupIdReturnsNull() {
        // Arrange
        TenantContext.setGroupId(10L);

        // Act
        TenantContext.clear();
        Long groupId = TenantContext.getGroupId();

        // Assert
        assertThat(groupId)
                .as("Group ID deve ser null após limpeza")
                .isNull();
    }

    // ===== Testes de Group IDs (Lista) =====

    @Test
    @DisplayName("dado_listaDeGroupIds_quando_setGroupIds_entao_getGroupIdsRetornaLista")
    void testGivenListOfGroupIds_WhenSetGroupIds_ThenGetGroupIdsReturnsList() {
        // Arrange
        List<Long> expectedGroupIds = Arrays.asList(1L, 2L, 3L);

        // Act
        TenantContext.setGroupIds(expectedGroupIds);
        List<Long> actualGroupIds = TenantContext.getGroupIds();

        // Assert
        assertThat(actualGroupIds)
                .as("Lista de Group IDs deve ser retornada corretamente")
                .isEqualTo(expectedGroupIds)
                .hasSize(3)
                .containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("dado_listaVaziaDeGroupIds_quando_setGroupIds_entao_getGroupIdsRetornaListaVazia")
    void testGivenEmptyListOfGroupIds_WhenSetGroupIds_ThenGetGroupIdsReturnsEmptyList() {
        // Arrange
        List<Long> expectedGroupIds = Arrays.asList(); // lista vazia

        // Act
        TenantContext.setGroupIds(expectedGroupIds);
        List<Long> actualGroupIds = TenantContext.getGroupIds();

        // Assert
        assertThat(actualGroupIds)
                .as("Lista de Group IDs deve estar vazia")
                .isEmpty();
    }

    @Test
    @DisplayName("dado_nenhunGroupIdsDefinido_quando_getGroupIds_entao_retornaNull")
    void testGivenNoGroupIdsDefined_WhenGetGroupIds_ThenReturnsNull() {
        // Act
        List<Long> groupIds = TenantContext.getGroupIds();

        // Assert
        assertThat(groupIds)
                .as("getGroupIds deve retornar null quando nenhum valor foi definido")
                .isNull();
    }

    @Test
    @DisplayName("dado_groupIdsDefinido_quando_clear_entao_getGroupIdsRetornaNull")
    void testGivenGroupIdsDefined_WhenClear_ThenGetGroupIdsReturnsNull() {
        // Arrange
        TenantContext.setGroupIds(Arrays.asList(1L, 2L, 3L));

        // Act
        TenantContext.clear();
        List<Long> groupIds = TenantContext.getGroupIds();

        // Assert
        assertThat(groupIds)
                .as("Group IDs deve ser null após limpeza")
                .isNull();
    }

    // ===== Testes de Limpeza Completa =====

    @Test
    @DisplayName("dado_todosOsCamposDefinidos_quando_clear_entao_todosOsValoresSaoNulificados")
    void testGivenAllFieldsDefined_WhenClear_ThenAllValuesNulled() {
        // Arrange - definir todos os campos
        TenantContext.setTenantId(1L);
        TenantContext.setGroupId(10L);
        TenantContext.setGroupIds(Arrays.asList(1L, 2L));

        // Act
        TenantContext.clear();

        // Assert - todos os campos devem estar null
        assertThat(TenantContext.getTenantId())
                .as("Tenant ID deve ser null após clear")
                .isNull();
        assertThat(TenantContext.getGroupId())
                .as("Group ID deve ser null após clear")
                .isNull();
        assertThat(TenantContext.getGroupIds())
                .as("Group IDs deve ser null após clear")
                .isNull();
    }

    @Test
    @DisplayName("dado_clearChamadoSemDefinicoes_quando_clear_entao_naoLancaExcecao")
    void testGivenClearCalledWithoutDefinitions_WhenClear_ThenNoExceptionThrown() {
        // Act & Assert - não deve lançar exceção
        assertThatNoException()
                .as("clear deve ser idempotente e não lançar exceção")
                .isThrownBy(() -> TenantContext.clear());
    }

    // ===== Testes Multi-Thread (ThreadLocal Isolation) =====

    @Test
    @DisplayName("dado_multiplosTenantEmThreadsDiferentes_quando_setTenantIdEmCadaThread_entao_contextoEhIsolado")
    void testGivenMultipleTenantInDifferentThreads_WhenSetTenantIdPerThread_ThenContextIsolated() {
        // Arrange
        Long tenantId1 = 1L;
        Long tenantId2 = 2L;
        AtomicReference<Long> thread1TenantId = new AtomicReference<>();
        AtomicReference<Long> thread2TenantId = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(2);

        // Thread 1 - Define tenantId = 1L
        Thread thread1 = new Thread(() -> {
            try {
                TenantContext.setTenantId(tenantId1);
                try {
                    Thread.sleep(100); // Simular processamento
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                thread1TenantId.set(TenantContext.getTenantId());
            } finally {
                latch.countDown();
            }
        });

        // Thread 2 - Define tenantId = 2L
        Thread thread2 = new Thread(() -> {
            try {
                TenantContext.setTenantId(tenantId2);
                try {
                    Thread.sleep(50); // Simular processamento
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                thread2TenantId.set(TenantContext.getTenantId());
            } finally {
                latch.countDown();
            }
        });

        // Act
        try {
            thread1.start();
            thread2.start();
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // Assert - cada thread deve ter seu próprio contexto isolado
        assertThat(thread1TenantId.get())
                .as("Thread 1 deve ter seu próprio TenantId isolado")
                .isEqualTo(tenantId1);
        assertThat(thread2TenantId.get())
                .as("Thread 2 deve ter seu próprio TenantId isolado")
                .isEqualTo(tenantId2);

        // Main thread não deve ser afetado
        assertThat(TenantContext.getTenantId())
                .as("Thread principal não deve ser afetada pelos valores das outras threads")
                .isNull();
    }

    @Test
    @DisplayName("dado_groupIdsDefinidoEmUmaThread_quando_anothaThreadTentaAcessar_entao_naoHaVazamento")
    void testGivenGroupIdsDefinedInOneThread_WhenAnotherThreadAccesses_ThenNoLeakage() {
        // Arrange
        List<Long> groupIds1 = Arrays.asList(1L, 2L);
        List<Long> groupIds2 = Arrays.asList(10L, 20L, 30L);
        AtomicReference<List<Long>> thread1GroupIds = new AtomicReference<>();
        AtomicReference<List<Long>> thread2GroupIds = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(2);

        // Thread 1 - Define groupIds diferentes
        Thread thread1 = new Thread(() -> {
            try {
                TenantContext.setGroupIds(groupIds1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                thread1GroupIds.set(TenantContext.getGroupIds());
            } finally {
                latch.countDown();
            }
        });

        // Thread 2 - Define groupIds diferentes depois
        Thread thread2 = new Thread(() -> {
            try {
                TenantContext.setGroupIds(groupIds2);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                thread2GroupIds.set(TenantContext.getGroupIds());
            } finally {
                latch.countDown();
            }
        });

        // Act
        try {
            thread1.start();
            thread2.start();
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // Assert - cada thread tem seus próprios groupIds
        assertThat(thread1GroupIds.get())
                .as("Thread 1 deve ter seus próprios groupIds")
                .isEqualTo(groupIds1)
                .hasSize(2);
        assertThat(thread2GroupIds.get())
                .as("Thread 2 deve ter seus próprios groupIds")
                .isEqualTo(groupIds2)
                .hasSize(3);
    }

    // ===== Testes de Limites =====

    @Test
    @DisplayName("dado_tenantIdMuitoGrande_quando_setTenantId_entao_valorEhArmazenadoCorretamente")
    void testGivenVeryLargeTenantId_WhenSetTenantId_ThenValueStoredCorrectly() {
        // Arrange
        Long largeId = Long.MAX_VALUE;

        // Act
        TenantContext.setTenantId(largeId);
        Long actualTenantId = TenantContext.getTenantId();

        // Assert
        assertThat(actualTenantId)
                .as("TenantId muito grande deve ser armazenado corretamente")
                .isEqualTo(largeId);
    }

    @Test
    @DisplayName("dado_tenantIdZero_quando_setTenantId_entao_valorEhArmazenadoCorretamente")
    void testGivenZeroTenantId_WhenSetTenantId_ThenValueStoredCorrectly() {
        // Arrange - zero é um valor válido
        Long zeroId = 0L;

        // Act
        TenantContext.setTenantId(zeroId);
        Long actualTenantId = TenantContext.getTenantId();

        // Assert
        assertThat(actualTenantId)
                .as("TenantId zero deve ser armazenado (sem validação de valor nesta classe)")
                .isEqualTo(zeroId);
    }

    @Test
    @DisplayName("dado_listaGrandeDeGroupIds_quando_setGroupIds_entao_todosOsValoresSaoPreservados")
    void testGivenLargeListOfGroupIds_WhenSetGroupIds_ThenAllValuesPreserved() {
        // Arrange
        List<Long> largeList = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        // Act
        TenantContext.setGroupIds(largeList);
        List<Long> actualGroupIds = TenantContext.getGroupIds();

        // Assert
        assertThat(actualGroupIds)
                .as("Lista grande de groupIds deve ser preservada completamente")
                .isEqualTo(largeList)
                .hasSize(10);
    }

    // ===== TESTES ADICIONAIS - Edge Cases e Validações =====

    @Test
    @DisplayName("dado_tenantIdComValorLongo_quando_setTenantId_entao_armazenaCorretamente")
    void testGivenLargeTenantId_WhenSetTenantId_ThenStoresCorrectly() {
        // Arrange
        Long largeTenantId = Long.MAX_VALUE;

        // Act
        TenantContext.setTenantId(largeTenantId);

        // Assert
        assertThat(TenantContext.getTenantId()).isEqualTo(largeTenantId);
    }

    @Test
    @DisplayName("dado_tenantIdComValorNumeroBaixo_quando_setTenantId_entao_armazena")
    void testGivenSmallTenantId_WhenSetTenantId_ThenStores() {
        // Arrange
        Long smallTenantId = 1L;

        // Act
        TenantContext.setTenantId(smallTenantId);

        // Assert
        assertThat(TenantContext.getTenantId()).isEqualTo(smallTenantId);
    }

    @Test
    @DisplayName("dado_groupIdListaVazia_quando_setGroupIds_entao_armazenaVazia")
    void testGivenEmptyGroupIdsList_WhenSetGroupIds_ThenStoresEmpty() {
        // Arrange
        List<Long> emptyList = Arrays.asList();

        // Act
        TenantContext.setGroupIds(emptyList);

        // Assert
        assertThat(TenantContext.getGroupIds())
                .as("Lista vazia deve ser armazenada")
                .isEmpty();
    }

    @Test
    @DisplayName("dado_contextoLimpoAposSetTenantId_quando_getTenantId_entaoRetornaNull")
    void testGivenContextClearedAfterSetTenantId_WhenGetTenantId_ThenReturnsNull() {
        // Arrange
        TenantContext.setTenantId(1L);
        
        // Act
        TenantContext.clear();

        // Assert
        assertThat(TenantContext.getTenantId()).isNull();
    }

    @Test
    @DisplayName("dado_contextoLimpoAposSetGroupIds_quando_getGroupIds_entaoRetornaNull")
    void testGivenContextClearedAfterSetGroupIds_WhenGetGroupIds_ThenReturnsNull() {
        // Arrange
        TenantContext.setGroupIds(Arrays.asList(1L, 2L, 3L));
        
        // Act
        TenantContext.clear();

        // Assert
        assertThat(TenantContext.getGroupIds()).isNull();
    }

    @Test
    @DisplayName("dado_tenantIdSobrescrito_quando_getTenantId_entaoRetornaNovoValor")
    void testGivenTenantIdOverwritten_WhenGetTenantId_ThenReturnsNewValue() {
        // Arrange
        TenantContext.setTenantId(1L);
        
        // Act
        TenantContext.setTenantId(999L);

        // Assert
        assertThat(TenantContext.getTenantId()).isEqualTo(999L);
    }

    @Test
    @DisplayName("dado_groupIdsSobrescrito_quando_getGroupIds_entaoRetornaNovaLista")
    void testGivenGroupIdsOverwritten_WhenGetGroupIds_ThenReturnsNewList() {
        // Arrange
        TenantContext.setGroupIds(Arrays.asList(1L, 2L));
        
        // Act
        TenantContext.setGroupIds(Arrays.asList(999L, 998L, 997L));

        // Assert
        assertThat(TenantContext.getGroupIds())
                .contains(999L, 998L, 997L)
                .hasSize(3);
    }

    @Test
    @DisplayName("dado_groupIdComNumeroDuplicado_quando_setGroupIds_entaoArmazenaComDuplicatas")
    void testGivenGroupIdsWithDuplicates_WhenSetGroupIds_ThenStoresWithDuplicates() {
        // Arrange
        List<Long> listWithDuplicates = Arrays.asList(1L, 1L, 2L, 2L, 3L);

        // Act
        TenantContext.setGroupIds(listWithDuplicates);

        // Assert
        assertThat(TenantContext.getGroupIds())
                .as("Duplicatas devem ser preservadas")
                .containsExactly(1L, 1L, 2L, 2L, 3L)
                .hasSize(5);
    }

    @Test
    @DisplayName("dado_multiplosClearsChamados_quando_getTenantId_entaoRetornaNull")
    void testGivenMultipleClearsCalled_WhenGetTenantId_ThenReturnsNull() {
        // Arrange
        TenantContext.setTenantId(1L);
        
        // Act
        TenantContext.clear();
        TenantContext.clear();
        TenantContext.clear();

        // Assert
        assertThat(TenantContext.getTenantId()).isNull();
    }

    @Test
    @DisplayName("dado_setTenantIdAposClear_quando_getTenantId_entaoRetornaNovoValor")
    void testGivenSetTenantIdAfterClear_WhenGetTenantId_ThenReturnsNewValue() {
        // Arrange
        TenantContext.setTenantId(1L);
        TenantContext.clear();
        
        // Act
        TenantContext.setTenantId(2L);

        // Assert
        assertThat(TenantContext.getTenantId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("dado_groupIdsSingleItem_quando_setGroupIds_entaoArmazena")
    void testGivenGroupIdsSingleItem_WhenSetGroupIds_ThenStores() {
        // Arrange
        List<Long> singleList = Arrays.asList(42L);

        // Act
        TenantContext.setGroupIds(singleList);

        // Assert
        assertThat(TenantContext.getGroupIds())
                .contains(42L)
                .hasSize(1);
    }

    @Test
    @DisplayName("dado_groupIdsComValoresNegativos_quando_setGroupIds_entaoArmazenaNegativos")
    void testGivenGroupIdsWithNegativeValues_WhenSetGroupIds_ThenStoresNegatives() {
        // Arrange
        List<Long> negativeList = Arrays.asList(-1L, -999L, -12345L);

        // Act
        TenantContext.setGroupIds(negativeList);

        // Assert
        assertThat(TenantContext.getGroupIds())
                .contains(-1L, -999L, -12345L)
                .hasSize(3);
    }

    @Test
    @DisplayName("dado_groupIdScriptComValoreZero_quando_setGroupIds_entaoArmazenaZero")
    void testGivenGroupIdsWithZero_WhenSetGroupIds_ThenStoresZero() {
        // Arrange
        List<Long> zeroList = Arrays.asList(0L, 1L, 2L);

        // Act
        TenantContext.setGroupIds(zeroList);

        // Assert
        assertThat(TenantContext.getGroupIds())
                .contains(0L, 1L, 2L)
                .hasSize(3);
    }

    @Test
    @DisplayName("dado_setGroupIdEntaoSetTenantId_quando_getTenantId_entaoRetornaTenantId")
    void testGivenSetGroupIdsThenSetTenantId_WhenGetTenantId_ThenReturnsTenantId() {
        // Arrange
        TenantContext.setGroupIds(Arrays.asList(1L, 2L));
        
        // Act
        TenantContext.setTenantId(999L);

        // Assert
        assertThat(TenantContext.getTenantId()).isEqualTo(999L);
        assertThat(TenantContext.getGroupIds()).contains(1L, 2L);
    }

    @Test
    @DisplayName("dado_setTenantIdEnquantoGetGroupIdsChamado_quando_getChamado_entaoRetornaAmbos")
    void testGivenSetBothThenGetBoth_WhenGetCalled_ThenReturnsBoth() {
        // Arrange & Act
        TenantContext.setTenantId(100L);
        TenantContext.setGroupIds(Arrays.asList(1L, 2L, 3L));

        // Assert
        assertThat(TenantContext.getTenantId()).isEqualTo(100L);
        assertThat(TenantContext.getGroupIds()).contains(1L, 2L, 3L);
    }

    @Test
    @DisplayName("dado_variosClearsChamadosCom3Threads_quando_getTenantId_entaoAllNull")
    void testGivenMultipleClearsWith3Threads_WhenGetTenantId_ThenAllNull() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(3);

        // Act
        Thread t1 = new Thread(() -> {
            try {
                TenantContext.setTenantId(1L);
                Thread.sleep(10);
                TenantContext.clear();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                TenantContext.setTenantId(2L);
                Thread.sleep(10);
                TenantContext.clear();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                TenantContext.setTenantId(3L);
                Thread.sleep(10);
                TenantContext.clear();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        t1.start();
        t2.start();
        t3.start();
        latch.await();

        // Assert
        assertThat(TenantContext.getTenantId()).isNull();
    }

    @Test
    @DisplayName("dado_tenantIdMaxValue_quando_setTenantId_entaoArmazenaMaxValue")
    void testGivenTenantIdMaxValue_WhenSetTenantId_ThenStoresMaxValue() {
        // Arrange
        Long maxValue = Long.MAX_VALUE;

        // Act
        TenantContext.setTenantId(maxValue);

        // Assert
        assertThat(TenantContext.getTenantId()).isEqualTo(maxValue);
    }

    @Test
    @DisplayName("dado_tenantIdMinValue_quando_setTenantId_entaoArmazenaMinValue")
    void testGivenTenantIdMinValue_WhenSetTenantId_ThenStoresMinValue() {
        // Arrange
        Long minValue = Long.MIN_VALUE;

        // Act
        TenantContext.setTenantId(minValue);

        // Assert
        assertThat(TenantContext.getTenantId()).isEqualTo(minValue);
    }

    @Test
    @DisplayName("dado_100GroupIds_quando_setGroupIds_entaoArmazenaTodos")
    void testGiven100GroupIds_WhenSetGroupIds_ThenStoresAll() {
        // Arrange
        List<Long> largeList = new java.util.ArrayList<>();
        for (long i = 1; i <= 100; i++) {
            largeList.add(i);
        }

        // Act
        TenantContext.setGroupIds(largeList);

        // Assert
        assertThat(TenantContext.getGroupIds())
                .as("Todos os 100 groupIds devem ser armazenados")
                .hasSize(100)
                .contains(1L, 50L, 100L);
    }

    @Test
    @DisplayName("dado_clearChamadoVariasVezesComDelay_quando_getTenantId_entaoNull")
    void testGivenClearCalledMultipleTimesWithDelay_WhenGetTenantId_ThenNull() {
        // Arrange
        TenantContext.setTenantId(1L);
        TenantContext.clear();
        
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        TenantContext.clear();

        // Assert
        assertThat(TenantContext.getTenantId()).isNull();
    }

    @Test
    @DisplayName("dado_groupIdListaRapidamenteSobrescrita_quando_getGroupIds_entaoRetornaUltima")
    void testGivenGroupIdsRapidlyOverwritten_WhenGetGroupIds_ThenReturnsLast() {
        // Arrange & Act
        TenantContext.setGroupIds(Arrays.asList(1L));
        TenantContext.setGroupIds(Arrays.asList(2L));
        TenantContext.setGroupIds(Arrays.asList(3L, 4L, 5L));

        // Assert
        assertThat(TenantContext.getGroupIds())
                .contains(3L, 4L, 5L)
                .hasSize(3)
                .doesNotContain(1L, 2L);
    }
}
