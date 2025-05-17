package com.example.productmgr.config;

import com.example.productmgr.model.SystemSetting;
import com.example.productmgr.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * すべてのコントローラーで共通のモデル属性を追加するためのControllerAdvice
 * これにより、すべてのビューで会社名などの共通情報が利用可能になる
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributesAdvice {

    private final SystemSettingService systemSettingService;

    /**
     * 会社名をすべてのモデルに追加
     * 
     * @return システム設定から取得した会社名
     */
    @ModelAttribute("companyName")
    public String getCompanyName() {
        return systemSettingService.getString(SystemSetting.COMPANY_NAME, "株式会社サンプル");
    }
}