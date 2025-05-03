package com.example.productmgr.controller;

import com.example.productmgr.model.SystemSetting;
import com.example.productmgr.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class SettingsController {
    
    private final SystemSettingService settingService;
    
    @GetMapping
    public String showSettings(Model model) {
        // 基本設定
        model.addAttribute("companyName", settingService.getValue(SystemSetting.COMPANY_NAME, ""));
        model.addAttribute("lowStockThreshold", settingService.getIntValue(SystemSetting.LOW_STOCK_THRESHOLD, 10));
        
        // 通知設定
        model.addAttribute("enableNotifications", settingService.getBooleanValue(SystemSetting.ENABLE_NOTIFICATIONS, false));
        model.addAttribute("notificationEmail", settingService.getValue(SystemSetting.NOTIFICATION_EMAIL, ""));
        
        // バックアップ設定
        model.addAttribute("autoBackup", settingService.getBooleanValue(SystemSetting.AUTO_BACKUP, false));
        model.addAttribute("backupIntervalDays", settingService.getIntValue(SystemSetting.BACKUP_INTERVAL_DAYS, 7));
        
        return "settings/index";
    }
    
    @PostMapping("/basic")
    public String saveBasicSettings(
            @RequestParam String companyName,
            @RequestParam int lowStockThreshold,
            RedirectAttributes redirectAttributes) {
        
        settingService.saveOrUpdate(SystemSetting.COMPANY_NAME, companyName, "会社名");
        settingService.saveOrUpdate(SystemSetting.LOW_STOCK_THRESHOLD, String.valueOf(lowStockThreshold), "在庫少警告のしきい値");
        
        redirectAttributes.addFlashAttribute("message", "基本設定を保存しました");
        return "redirect:/settings";
    }
    
    @PostMapping("/notification")
    public String saveNotificationSettings(
            @RequestParam(defaultValue = "false") boolean enableNotifications,
            @RequestParam String notificationEmail,
            RedirectAttributes redirectAttributes) {
        
        settingService.saveOrUpdate(SystemSetting.ENABLE_NOTIFICATIONS, String.valueOf(enableNotifications), "通知機能の有効化");
        settingService.saveOrUpdate(SystemSetting.NOTIFICATION_EMAIL, notificationEmail, "通知用メールアドレス");
        
        redirectAttributes.addFlashAttribute("message", "通知設定を保存しました");
        return "redirect:/settings";
    }
    
    @PostMapping("/backup")
    public String saveBackupSettings(
            @RequestParam(defaultValue = "false") boolean autoBackup,
            @RequestParam int backupIntervalDays,
            RedirectAttributes redirectAttributes) {
        
        settingService.saveOrUpdate(SystemSetting.AUTO_BACKUP, String.valueOf(autoBackup), "自動バックアップの有効化");
        settingService.saveOrUpdate(SystemSetting.BACKUP_INTERVAL_DAYS, String.valueOf(backupIntervalDays), "バックアップ間隔（日）");
        
        redirectAttributes.addFlashAttribute("message", "バックアップ設定を保存しました");
        return "redirect:/settings";
    }
}