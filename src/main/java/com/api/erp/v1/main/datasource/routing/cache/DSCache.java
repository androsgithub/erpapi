package com.api.erp.v1.main.datasource.routing.cache;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class DSCache {
    private final Map<Long, DataSource> cache = new ConcurrentHashMap<>();

    public DataSource get(Long tenantId) {
        return cache.get(tenantId);
    }

    public void put(Long tenantId, DataSource dataSource) {
        cache.put(tenantId, dataSource);
    }

    public boolean contains(Long tenantId) {
        return cache.containsKey(tenantId);
    }

    public DataSource remove(Long tenantId) {
        DataSource ds = cache.remove(tenantId);

        closeIfNecessary(ds);
        return ds;
    }

    public Map<Long, DataSource> getAll() {
        return Collections.unmodifiableMap(cache);
    }

    public DataSource computeIfAbsent(Long tenantId, java.util.function.Function<Long, DataSource> factory) {
        return cache.computeIfAbsent(tenantId, factory);
    }

    private void closeIfNecessary(DataSource ds) {
        if (ds instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }
}
