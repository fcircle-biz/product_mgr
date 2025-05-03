package com.example.productmgr.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("system_settings")
public class SystemSetting {
    
    @Id
    private Long id;
    private String key;
    private String value;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // メモリ上の設定キャッシュ用に使用される定数
    public static final String COMPANY_NAME = "company_name";
    public static final String LOW_STOCK_THRESHOLD = "low_stock_threshold";
    public static final String ENABLE_NOTIFICATIONS = "enable_notifications";
    public static final String NOTIFICATION_EMAIL = "notification_email";
    public static final String AUTO_BACKUP = "auto_backup";
    public static final String BACKUP_INTERVAL_DAYS = "backup_interval_days";
    public static final String LAST_BACKUP_DATE = "last_backup_date";
}