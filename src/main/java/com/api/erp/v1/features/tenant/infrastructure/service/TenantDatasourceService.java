package com.api.erp.v1.features.tenant.infrastructure.service;

import com.api.erp.v1.features.tenant.application.dto.TenantDatasourceRequest;
import com.api.erp.v1.features.tenant.application.dto.TenantDatasourceResponse;
import com.api.erp.v1.features.tenant.domain.entity.Tenant;
import com.api.erp.v1.features.tenant.domain.entity.TenantDatasource;
import com.api.erp.v1.features.tenant.domain.repository.TenantDatasourceRepository;
import com.api.erp.v1.features.tenant.domain.repository.TenantRepository;
import com.api.erp.v1.features.tenant.domain.service.ITenantDatasourceService;
import com.api.erp.v1.shared.infrastructure.config.datasource.routing.DataSourceFactory;
import com.api.erp.v1.shared.infrastructure.config.datasource.routing.MultiTenantRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * TenantSchemaService
 * 
 * Implementação de serviço para gerenciar configurações de datasources dos tenants.
 * 
 * Responsabilidades:
 * - Configurar novo datasource para um tenant
 * - Atualizar configuração de datasource
 * - Testar conexão com banco de dados
 * - Registrar datasource no MultiTenantRoutingDataSource
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantDatasourceService implements ITenantDatasourceService {

    private final TenantRepository tenantRepository;
    private final TenantDatasourceRepository tenantDatasourceRepository;
    private final DataSourceFactory dataSourceFactory;
    private final MultiTenantRoutingDataSource multiTenantRoutingDataSource;

    /**
     * Configura novo datasource para um tenant
     */
    @Override
    @Transactional
    public TenantDatasourceResponse configurarDatasource(String tenantSlug, TenantDatasourceRequest request) {
        log.info("Configurando datasource para tenant: {}", tenantSlug);

        // 1. Buscar tenant
        Tenant tenant = tenantRepository.findByNome(tenantSlug)
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado: " + tenantSlug));

        // 2. Validar se já existe datasource configurado
        if (tenantDatasourceRepository.existsByTenant_IdAndIsActive(tenant.getId(), true)) {
            throw new IllegalStateException("Datasource já configurado para este tenant");
        }

        // 3. Testar conexão antes de salvar
        if (!testarConexao(request)) {
            log.error("Falha ao testar conexão com banco de dados para tenant: {}", tenantSlug);
            throw new RuntimeException("Falha ao conectar com o banco de dados. Verifique as credenciais.");
        }

        // 4. Criar e salvar TenantDatasource
        TenantDatasource tenantDatasource = TenantDatasource.builder()
                .tenant(tenant)
                .host(request.host())
                .port(request.port())
                .databaseName(request.databaseName())
                .username(request.username())
                .password(request.password())
                .driverClassName(request.driverClassName())
                .dialect(request.dialect())
                .isActive(true)
                .testStatus(TenantDatasource.TestStatus.SUCCESS)
                .testedAt(LocalDateTime.now())
                .build();

        tenantDatasource = tenantDatasourceRepository.save(tenantDatasource);
        log.info("Datasource configurado com sucesso para tenant: {} | ID: {}", tenantSlug, tenant.getId());

        // 5. Registrar no MultiTenantRoutingDataSource
        DataSource dataSource = dataSourceFactory.createDataSourceFromConfig(tenantDatasource);
        multiTenantRoutingDataSource.addDataSource(tenantSlug, dataSource);

        return toResponse(tenantDatasource);
    }

    /**
     * Obtém configuração de datasource de um tenant
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TenantDatasourceResponse> obterDatasource(String tenantSlug) {
        log.debug("Buscando datasource para tenant: {}", tenantSlug);

        return tenantRepository.findByNome(tenantSlug)
                .flatMap(tenant -> tenantDatasourceRepository.findByTenant_IdAndIsActive(tenant.getId(), true))
                .map(this::toResponse);
    }

    /**
     * Atualiza configuração de datasource de um tenant
     */
    @Override
    @Transactional
    public TenantDatasourceResponse atualizarDatasource(String tenantSlug, TenantDatasourceRequest request) {
        log.info("Atualizando datasource para tenant: {}", tenantSlug);

        // 1. Buscar tenant
        Tenant tenant = tenantRepository.findByNome(tenantSlug)
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado: " + tenantSlug));

        // 2. Buscar datasource ativo
        TenantDatasource tenantDatasource = tenantDatasourceRepository
                .findByTenant_IdAndIsActive(tenant.getId(), true)
                .orElseThrow(() -> new IllegalArgumentException("Datasource não configurado para este tenant"));

        // 3. Testar nova conexão
        if (!testarConexao(request)) {
            log.error("Falha ao testar nova configuração de datasource para tenant: {}", tenantSlug);
            throw new RuntimeException("Falha ao conectar com o novo banco de dados");
        }

        // 4. Atualizar entidade
        tenantDatasource.setHost(request.host());
        tenantDatasource.setPort(request.port());
        tenantDatasource.setDatabaseName(request.databaseName());
        tenantDatasource.setUsername(request.username());
        tenantDatasource.setPassword(request.password());
        tenantDatasource.setDriverClassName(request.driverClassName());
        tenantDatasource.setDialect(request.dialect());
        tenantDatasource.setTestStatus(TenantDatasource.TestStatus.SUCCESS);
        tenantDatasource.setTestedAt(LocalDateTime.now());

        tenantDatasource = tenantDatasourceRepository.save(tenantDatasource);
        log.info("Datasource atualizado com sucesso para tenant: {}", tenantSlug);

        // 5. Atualizar no MultiTenantRoutingDataSource
        DataSource dataSource = dataSourceFactory.createDataSourceFromConfig(tenantDatasource);
        multiTenantRoutingDataSource.addDataSource(tenantSlug, dataSource);

        return toResponse(tenantDatasource);
    }

    /**
     * Testa conexão com banco de dados
     */
    @Override
    public boolean testarConexao(TenantDatasourceRequest request) {
        log.debug("Testando conexão com banco: {}:{}/{}", request.host(), request.port(), request.databaseName());

        try {
            // Criar datasource temporário para teste
            TenantDatasource tempConfig = TenantDatasource.builder()
                    .host(request.host())
                    .port(request.port())
                    .databaseName(request.databaseName())
                    .username(request.username())
                    .password(request.password())
                    .driverClassName(request.driverClassName())
                    .dialect(request.dialect())
                    .build();

            DataSource testDataSource = dataSourceFactory.createDataSourceFromConfig(tempConfig);
            
            // Tentar obter conexão
            try (Connection connection = testDataSource.getConnection()) {
                log.debug("Conexão com banco de dados testada com sucesso");
                // Fechar datasource se for HikariCP
                if (testDataSource instanceof HikariDataSource) {
                    ((HikariDataSource) testDataSource).close();
                }
                return true;
            }
        } catch (SQLException e) {
            log.error("Erro ao testar conexão com banco de dados", e);
            return false;
        }
    }

    /**
     * Converte TenantDatasource para TenantDatasourceResponse
     */
    private TenantDatasourceResponse toResponse(TenantDatasource tenantDatasource) {
        return new TenantDatasourceResponse(
                tenantDatasource.getId(),
                tenantDatasource.getTenant().getId(),
                tenantDatasource.getHost(),
                tenantDatasource.getPort(),
                tenantDatasource.getDatabaseName(),
                tenantDatasource.getUsername(),
                tenantDatasource.getDriverClassName(),
                tenantDatasource.getDialect(),
                tenantDatasource.getIsActive(),
                tenantDatasource.getCreatedAt(),
                tenantDatasource.getUpdatedAt()
        );
    }
}
