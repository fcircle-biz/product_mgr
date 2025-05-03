package com.example.productmgr.service;

import com.example.productmgr.model.SystemSetting;
import com.example.productmgr.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemSettingService {
    
    private final SystemSettingRepository systemSettingRepository;
    
    // メモリ内キャッシュ
    private final Map<String, String> settingsCache = new HashMap<>();
    
    /**
     * 全ての設定を取得する
     */
    public List<SystemSetting> findAll() {
        return (List<SystemSetting>) systemSettingRepository.findAll();
    }
    
    /**
     * 指定されたキーの設定値を取得する
     * キャッシュがある場合はキャッシュから取得する
     */
    public String getValue(String key, String defaultValue) {
        if (settingsCache.containsKey(key)) {
            return settingsCache.get(key);
        }
        
        Optional<SystemSetting> setting = systemSettingRepository.findByKey(key);
        if (setting.isPresent()) {
            settingsCache.put(key, setting.get().getValue());
            return setting.get().getValue();
        }
        
        return defaultValue;
    }
    
    /**
     * 指定されたキーの設定値を文字列として取得する (getValue のエイリアス)
     */
    public String getString(String key, String defaultValue) {
        return getValue(key, defaultValue);
    }
    
    /**
     * 指定されたキーの設定値を数値として取得する
     */
    public int getIntValue(String key, int defaultValue) {
        String value = getValue(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 指定されたキーの設定値を真偽値として取得する
     */
    public boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getValue(key, String.valueOf(defaultValue));
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }
    
    /**
     * 設定値を保存または更新する
     */
    @Transactional
    public SystemSetting saveOrUpdate(String key, String value, String description) {
        Optional<SystemSetting> existingSetting = systemSettingRepository.findByKey(key);
        
        SystemSetting setting;
        if (existingSetting.isPresent()) {
            setting = existingSetting.get();
            setting.setValue(value);
            setting.setUpdatedAt(LocalDateTime.now());
        } else {
            setting = new SystemSetting();
            setting.setKey(key);
            setting.setValue(value);
            setting.setDescription(description);
            setting.setCreatedAt(LocalDateTime.now());
            setting.setUpdatedAt(LocalDateTime.now());
        }
        
        SystemSetting savedSetting = systemSettingRepository.save(setting);
        
        // キャッシュを更新
        settingsCache.put(key, value);
        
        return savedSetting;
    }
    
    /**
     * 初期設定値をロードする
     * アプリケーション起動時に呼び出される
     */
    @Transactional
    public void loadDefaultSettings() {
        // 会社名
        if (!systemSettingRepository.existsByKey(SystemSetting.COMPANY_NAME)) {
            saveOrUpdate(SystemSetting.COMPANY_NAME, "株式会社サンプル", "会社名");
        }
        
        // 在庫少警告のしきい値
        if (!systemSettingRepository.existsByKey(SystemSetting.LOW_STOCK_THRESHOLD)) {
            saveOrUpdate(SystemSetting.LOW_STOCK_THRESHOLD, "10", "在庫少警告のしきい値");
        }
        
        // 通知機能の有効化
        if (!systemSettingRepository.existsByKey(SystemSetting.ENABLE_NOTIFICATIONS)) {
            saveOrUpdate(SystemSetting.ENABLE_NOTIFICATIONS, "false", "通知機能の有効化");
        }
        
        // 通知用メールアドレス
        if (!systemSettingRepository.existsByKey(SystemSetting.NOTIFICATION_EMAIL)) {
            saveOrUpdate(SystemSetting.NOTIFICATION_EMAIL, "", "通知用メールアドレス");
        }
        
        // 自動バックアップの有効化
        if (!systemSettingRepository.existsByKey(SystemSetting.AUTO_BACKUP)) {
            saveOrUpdate(SystemSetting.AUTO_BACKUP, "false", "自動バックアップの有効化");
        }
        
        // バックアップ間隔（日）
        if (!systemSettingRepository.existsByKey(SystemSetting.BACKUP_INTERVAL_DAYS)) {
            saveOrUpdate(SystemSetting.BACKUP_INTERVAL_DAYS, "7", "バックアップ間隔（日）");
        }
        
        // 最終バックアップ日
        if (!systemSettingRepository.existsByKey(SystemSetting.LAST_BACKUP_DATE)) {
            saveOrUpdate(SystemSetting.LAST_BACKUP_DATE, "2025/05/03", "最終バックアップ日");
        }
        
        // すべての設定をキャッシュに読み込む
        findAll().forEach(setting -> settingsCache.put(setting.getKey(), setting.getValue()));
    }
}