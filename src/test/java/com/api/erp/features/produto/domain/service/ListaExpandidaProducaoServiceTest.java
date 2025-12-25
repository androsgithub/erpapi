package com.api.erp.features.produto.domain.service;

import com.api.erp.v1.features.produto.domain.entity.Produto;
import com.api.erp.v1.features.produto.domain.entity.ProdutoComposicao;
import com.api.erp.v1.features.produto.domain.entity.StatusProduto;
import com.api.erp.v1.features.produto.domain.entity.TipoProduto;
import com.api.erp.v1.features.produto.domain.exception.ProdutoException;
import com.api.erp.v1.features.produto.domain.repository.ProdutoComposicaoRepository;
import com.api.erp.v1.features.produto.domain.service.ListaExpandidaProducaoService;
import com.api.erp.v1.features.unidademedida.domain.entity.UnidadeMedida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para ListaExpandidaProducaoService
 * 
 * Demonstra:
 * - Fácil testabilidade via DIP (mockagem de dependências)
 * - Cenários complexos de composição aninhada
 * - Validação de comportamento do domínio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ListaExpandidaProducaoService - Testes de Domínio")
class ListaExpandidaProducaoServiceTest {
    
    @Mock
    private ProdutoComposicaoRepository composicaoRepository;
    
    @InjectMocks
    private ListaExpandidaProducaoService service;
    
    private UnidadeMedida unidadeKG;
    private Produto produtoCompradoA;
    private Produto produtoCompradoB;
    private Produto produtoFabricavelC;
    
    @BeforeEach
    void setUp() {
        // Setup de dados de teste
        unidadeKG = UnidadeMedida.builder()
                .id(1L)
                .sigla("KG")
                .descricao("Quilograma")
                .tipo("MASSA")
                .ativo(true)
                .build();
        
        produtoCompradoA = Produto.builder()
                .id(1L)
                .codigo("MAT-001")
                .descricao("Aço Carbono")
                .tipo(TipoProduto.COMPRADO)
                .status(StatusProduto.ATIVO)
                .unidadeMedida(unidadeKG)
                .build();
        
        produtoCompradoB = Produto.builder()
                .id(2L)
                .codigo("MAT-002")
                .descricao("Tinta Epóxi")
                .tipo(TipoProduto.COMPRADO)
                .status(StatusProduto.ATIVO)
                .unidadeMedida(unidadeKG)
                .build();
        
        produtoFabricavelC = Produto.builder()
                .id(3L)
                .codigo("PROD-001")
                .descricao("Placa Base")
                .tipo(TipoProduto.FABRICAVEL)
                .status(StatusProduto.ATIVO)
                .unidadeMedida(unidadeKG)
                .build();
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando produto não é fabricável")
    void testProductoNaoFabricavel() {
        // Arrange
        BigDecimal quantidade = BigDecimal.valueOf(10);
        
        // Act & Assert
        assertThrows(ProdutoException.class, () -> {
            service.calcularListaExpandida(produtoCompradoA, quantidade);
        });
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando quantidade é inválida")
    void testQuantidadeInvalida() {
        // Arrange
        BigDecimal quantidadeInvalida = BigDecimal.ZERO;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            service.calcularListaExpandida(produtoFabricavelC, quantidadeInvalida);
        });
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia para produto sem composição")
    void testProdutoSemComposicao() {
        // Arrange
        when(composicaoRepository.findByProdutoFabricadoId(3L))
                .thenReturn(Collections.emptyList());
        
        // Act
        Map<Produto, BigDecimal> resultado = service.calcularListaExpandida(
                produtoFabricavelC,
                BigDecimal.ONE
        );
        
        // Assert
        assertTrue(resultado.isEmpty(), "Produto sem composição deve retornar mapa vazio");
    }
    
    @Test
    @DisplayName("Deve calcular composição simples corretamente")
    void testComposicaoSimples() {
        // Arrange
        ProdutoComposicao composicao1 = ProdutoComposicao.builder()
                .id(1L)
                .produtoFabricado(produtoFabricavelC)
                .produtoComponente(produtoCompradoA)
                .quantidadeNecessaria(BigDecimal.valueOf(2.5))
                .build();
        
        when(composicaoRepository.findByProdutoFabricadoId(3L))
                .thenReturn(List.of(composicao1));
        
        // Act
        Map<Produto, BigDecimal> resultado = service.calcularListaExpandida(
                produtoFabricavelC,
                BigDecimal.valueOf(10)
        );
        
        // Assert
        assertEquals(1, resultado.size());
        assertEquals(0, BigDecimal.valueOf(25).compareTo(resultado.get(produtoCompradoA)));
        // 10 (quantidade) x 2.5 (composição) = 25
    }
    
    @Test
    @DisplayName("Deve calcular composição aninhada corretamente")
    void testComposicaoAninhada() {
        // Arrange
        // Produto PROD-002 (fabricável) usa MAT-001 (comprado) e MAT-002 (comprado)
        Produto produtoFabricavelD = Produto.builder()
                .id(4L)
                .codigo("PROD-002")
                .descricao("Estrutura Soldada")
                .tipo(TipoProduto.FABRICAVEL)
                .status(StatusProduto.ATIVO)
                .unidadeMedida(unidadeKG)
                .build();
        
        // PROD-001 usa PROD-002 (2x) e MAT-002 (1x)
        ProdutoComposicao comp1 = ProdutoComposicao.builder()
                .id(1L)
                .produtoFabricado(produtoFabricavelC)
                .produtoComponente(produtoFabricavelD)
                .quantidadeNecessaria(BigDecimal.valueOf(2))
                .build();
        
        ProdutoComposicao comp2 = ProdutoComposicao.builder()
                .id(2L)
                .produtoFabricado(produtoFabricavelC)
                .produtoComponente(produtoCompradoB)
                .quantidadeNecessaria(BigDecimal.ONE)
                .build();
        
        // PROD-002 usa MAT-001 (3x)
        ProdutoComposicao comp3 = ProdutoComposicao.builder()
                .id(3L)
                .produtoFabricado(produtoFabricavelD)
                .produtoComponente(produtoCompradoA)
                .quantidadeNecessaria(BigDecimal.valueOf(3))
                .build();
        
        when(composicaoRepository.findByProdutoFabricadoId(3L))
                .thenReturn(List.of(comp1, comp2));
        
        when(composicaoRepository.findByProdutoFabricadoId(4L))
                .thenReturn(List.of(comp3));
        
        // Act
        Map<Produto, BigDecimal> resultado = service.calcularListaExpandida(
                produtoFabricavelC,
                BigDecimal.ONE
        );
        
        // Assert
        // MAT-001: 2 (PROD-002) x 3 (composição) = 6
        assertEquals(BigDecimal.valueOf(6), resultado.get(produtoCompradoA));
        
        // MAT-002: 1 (direto) = 1
        assertEquals(BigDecimal.ONE, resultado.get(produtoCompradoB));
        
        // Total de produtos distintos
        assertEquals(2, resultado.size());
    }
    
    @Test
    @DisplayName("Deve acumular quantidades de produtos duplicados")
    void testAcumulacaoQuantidades() {
        // Arrange
        // Produto usa MAT-001 duas vezes em composições diferentes
        ProdutoComposicao comp1 = ProdutoComposicao.builder()
                .id(1L)
                .produtoFabricado(produtoFabricavelC)
                .produtoComponente(produtoCompradoA)
                .quantidadeNecessaria(BigDecimal.valueOf(2))
                .sequencia(1)
                .build();
        
        ProdutoComposicao comp2 = ProdutoComposicao.builder()
                .id(2L)
                .produtoFabricado(produtoFabricavelC)
                .produtoComponente(produtoCompradoA)
                .quantidadeNecessaria(BigDecimal.valueOf(3))
                .sequencia(2)
                .build();
        
        when(composicaoRepository.findByProdutoFabricadoId(3L))
                .thenReturn(List.of(comp1, comp2));
        
        // Act
        Map<Produto, BigDecimal> resultado = service.calcularListaExpandida(
                produtoFabricavelC,
                BigDecimal.ONE
        );
        
        // Assert
        // As duas composições devem ser somadas: 2 + 3 = 5
        assertEquals(BigDecimal.valueOf(5), resultado.get(produtoCompradoA));
        assertEquals(1, resultado.size());
    }
    
    @Test
    @DisplayName("Deve multiplicar quantidades corretamente")
    void testMultiplicacaoQuantidades() {
        // Arrange
        ProdutoComposicao composicao = ProdutoComposicao.builder()
                .id(1L)
                .produtoFabricado(produtoFabricavelC)
                .produtoComponente(produtoCompradoA)
                .quantidadeNecessaria(BigDecimal.valueOf(2.5))
                .build();
        
        when(composicaoRepository.findByProdutoFabricadoId(3L))
                .thenReturn(List.of(composicao));
        
        // Act - Solicitando 100 unidades
        Map<Produto, BigDecimal> resultado = service.calcularListaExpandida(
                produtoFabricavelC,
                BigDecimal.valueOf(100)
        );
        
        // Assert - Deve ser 100 x 2.5 = 250
        assertEquals(0, BigDecimal.valueOf(250).compareTo(resultado.get(produtoCompradoA)));
    }
    
    @Test
    @DisplayName("Deve retornar apenas produtos comprados na lista de compras")
    void testListaCompras() {
        // Arrange
        Produto produtoFabricavelD = Produto.builder()
                .id(4L)
                .codigo("PROD-002")
                .descricao("Estrutura")
                .tipo(TipoProduto.FABRICAVEL)
                .status(StatusProduto.ATIVO)
                .unidadeMedida(unidadeKG)
                .build();
        
        ProdutoComposicao comp1 = ProdutoComposicao.builder()
                .id(1L)
                .produtoFabricado(produtoFabricavelC)
                .produtoComponente(produtoFabricavelD)
                .quantidadeNecessaria(BigDecimal.ONE)
                .build();
        
        ProdutoComposicao comp2 = ProdutoComposicao.builder()
                .id(2L)
                .produtoFabricado(produtoFabricavelD)
                .produtoComponente(produtoCompradoA)
                .quantidadeNecessaria(BigDecimal.valueOf(2))
                .build();
        
        when(composicaoRepository.findByProdutoFabricadoId(3L))
                .thenReturn(List.of(comp1));
        when(composicaoRepository.findByProdutoFabricadoId(4L))
                .thenReturn(List.of(comp2));
        
        // Act
        Map<Produto, BigDecimal> listaCompras = service.obterListaCompras(
                produtoFabricavelC,
                BigDecimal.ONE
        );
        
        // Assert
        assertTrue(listaCompras.containsKey(produtoCompradoA));
        assertFalse(listaCompras.containsKey(produtoFabricavelD));
        assertEquals(1, listaCompras.size());
    }
}
