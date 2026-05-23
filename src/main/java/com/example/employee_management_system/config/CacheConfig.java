package com.example.employee_management_system.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URISyntaxException;

/// by me
/// class to configure cache
/// @Configuration - marks the class as a configuration class
/// @EnableCaching - enables caching in the application
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    /// by me
    /// method to configure cache
    /// @Bean - marks the method as a bean
    /// JCacheCacheManager - cache manager implementation
    /// CacheManager - cache manager
    /// the difference between CacheManager and JCacheCacheManager is that CacheManager is the interface and JCacheCacheManager is the implementation
    /// CachingProvider is the interface for the caching provider is used to get the cache manager
    public JCacheCacheManager cacheManager() throws URISyntaxException {
        CachingProvider provider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        javax.cache.CacheManager cacheManager = provider.getCacheManager(
                getClass().getResource("/ehcache.xml").toURI(),
                getClass().getClassLoader()
        );
        return new JCacheCacheManager(cacheManager);
    }
}
