package br.edu.fernandotelheiro.continuumcontrol.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig implements CachingConfigurer {

    private static final Logger log =
            LoggerFactory.getLogger(CacheConfig.class);

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(
                    RuntimeException exception,
                    Cache cache,
                    Object key
            ) {
                log.warn(
                        "Falha ao ler cache {} para chave {}: {}",
                        cache.getName(),
                        key,
                        exception.getMessage()
                );
            }

            @Override
            public void handleCachePutError(
                    RuntimeException exception,
                    Cache cache,
                    Object key,
                    Object value
            ) {
                log.warn(
                        "Falha ao gravar cache {} para chave {}: {}",
                        cache.getName(),
                        key,
                        exception.getMessage()
                );
            }

            @Override
            public void handleCacheEvictError(
                    RuntimeException exception,
                    Cache cache,
                    Object key
            ) {
                log.warn(
                        "Falha ao remover entrada do cache {} para chave {}: {}",
                        cache.getName(),
                        key,
                        exception.getMessage()
                );
            }

            @Override
            public void handleCacheClearError(
                    RuntimeException exception,
                    Cache cache
            ) {
                log.warn(
                        "Falha ao limpar cache {}: {}",
                        cache.getName(),
                        exception.getMessage()
                );
            }
        };
    }
}