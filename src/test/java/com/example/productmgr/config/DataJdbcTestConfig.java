package com.example.productmgr.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class DataJdbcTestConfig {
    
    @Bean
    @Primary
    public NamingStrategy testNamingStrategy() {
        return new org.springframework.data.relational.core.mapping.DefaultNamingStrategy();
    }
    
    @Bean
    @Primary
    public JdbcMappingContext testJdbcMappingContext(NamingStrategy namingStrategy) {
        return new JdbcMappingContext(namingStrategy);
    }
}