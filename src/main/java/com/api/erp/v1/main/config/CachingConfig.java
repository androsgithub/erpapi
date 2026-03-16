package com.api.erp.v1.main.config;

import com.api.erp.v1.main.datasource.routing.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class CachingConfig {
    @Bean("principalKeyGenerator")
    public KeyGenerator principalKeyGenerator() {
        return (target, method, params) -> {

            String tenantId = TenantContext.getTenantId().toString();

            String groups = TenantContext.getGroupIds()
                    .stream()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            String parameters = Arrays.stream(params)
                    .map(this::normalizeParam)
                    .collect(Collectors.joining(":"));

            return tenantId + ":" +
                    groups + ":" +
                    parameters;
        };
    }

    private String normalizeParam(Object param) {

        if (param instanceof Pageable pageable) {
            return pageable.getPageNumber() + "-" +
                    pageable.getPageSize() + "-" +
                    pageable.getSort().toString();
        }

        return String.valueOf(param);
    }


    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("itemCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("customerCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
