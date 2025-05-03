package com.example.productmgr.config;

import com.example.productmgr.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    
    private final SystemSettingService systemSettingService;
    
    @Bean
    public CommandLineRunner initSettings() {
        return args -> {
            // アプリケーション起動時にデフォルト設定をロード
            systemSettingService.loadDefaultSettings();
        };
    }
}