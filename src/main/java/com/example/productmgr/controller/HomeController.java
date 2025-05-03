package com.example.productmgr.controller;

import com.example.productmgr.model.Product;
import com.example.productmgr.model.SystemSetting;
import com.example.productmgr.service.ProductService;
import com.example.productmgr.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final SystemSettingService systemSettingService;
    
    @GetMapping("/")
    public String home(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            model.addAttribute("username", user.getUsername());
            
            // 在庫切れ商品
            List<Product> outOfStockProducts = productService.findOutOfStock();
            model.addAttribute("outOfStockCount", outOfStockProducts.size());
            
            // 在庫少商品
            List<Product> lowStockProducts = productService.findLowStock();
            model.addAttribute("lowStockCount", lowStockProducts.size());
            
            // 全商品数
            List<Product> allProducts = productService.findAll();
            model.addAttribute("productCount", allProducts.size());
            
            // 最終ログイン日時のフォーマット
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            model.addAttribute("lastLoginTime", LocalDateTime.now().format(formatter));
            
            // システム状態
            model.addAttribute("databaseStatus", "良好");
            
            // 最終バックアップ日時
            String lastBackupDate = systemSettingService.getString("last_backup_date", "2025/05/03");
            model.addAttribute("lastBackupDate", lastBackupDate);
            
            // システム負荷
            model.addAttribute("systemLoad", "低");
            
            // 今日の取引数
            model.addAttribute("todayTransactions", 0); // 仮の値
            
            // 会社名
            String companyName = systemSettingService.getString(SystemSetting.COMPANY_NAME, "株式会社サンプル");
            model.addAttribute("companyName", companyName);
            
            return "main_menu";
        } else {
            return "redirect:/login";
        }
    }
}