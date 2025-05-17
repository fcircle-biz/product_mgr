package com.example.productmgr.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.CommandLineRunner;

@TestConfiguration
@Profile("test")
public class TestApplicationConfig {
    
    @Bean
    public CommandLineRunner initSettings() {
        // テスト環境では初期設定をスキップ
        return args -> {};
    }
}