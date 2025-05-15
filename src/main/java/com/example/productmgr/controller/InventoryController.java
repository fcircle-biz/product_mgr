package com.example.productmgr.controller;

import com.example.productmgr.model.InventoryHistory;
import com.example.productmgr.model.Product;
import com.example.productmgr.service.InventoryService;
import com.example.productmgr.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final ProductService productService;
    private final InventoryService inventoryService;
    
    @GetMapping
    public String listInventory(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) String stockStatus,
                                Model model) {
        List<Product> products;
        
        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchByKeyword(keyword);
            model.addAttribute("searchKeyword", keyword);
        } else if (categoryId != null) {
            products = productService.findByCategoryId(categoryId);
            model.addAttribute("selectedCategoryId", categoryId);
        } else if ("out_of_stock".equals(stockStatus)) {
            products = productService.findOutOfStock();
            model.addAttribute("selectedStockStatus", stockStatus);
        } else if ("low_stock".equals(stockStatus)) {
            products = productService.findLowStock();
            model.addAttribute("selectedStockStatus", stockStatus);
        } else {
            products = productService.findAll();
        }
        
        model.addAttribute("products", products);
        
        return "inventory/list";
    }
    
    @GetMapping("/history/{productId}")
    public String viewInventoryHistory(@PathVariable Long productId, Model model) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
        
        List<InventoryHistory> histories = inventoryService.findByProductId(productId);
        
        model.addAttribute("product", product);
        model.addAttribute("inventoryHistories", histories);
        
        return "inventory/history";
    }
    
    @GetMapping("/add")
    public String addInventoryForm(Model model) {
        model.addAttribute("inventoryHistory", new InventoryHistory());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("type", "入庫");
        model.addAttribute("reasons", getAddReasons());
        
        return "inventory/add";
    }
    
    @GetMapping("/subtract")
    public String subtractInventoryForm(Model model) {
        model.addAttribute("inventoryHistory", new InventoryHistory());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("type", "出庫");
        model.addAttribute("reasons", getSubtractReasons());
        
        return "inventory/subtract";
    }
    
    @PostMapping("/process")
    public String processInventory(@Valid InventoryHistory inventoryHistory,
                                   BindingResult result,
                                   @AuthenticationPrincipal User user,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.findAll());
            return "入庫".equals(inventoryHistory.getType()) ? "inventory/add" : "inventory/subtract";
        }
        
        try {
            // Spring Securityのユーザー名からユーザーIDを取得
            // 簡易実装として管理者ユーザー（ID:1）を使用
            if (user != null && "admin".equals(user.getUsername())) {
                inventoryHistory.setUserId(1L);
            } else {
                // デフォルトは管理者ユーザー
                inventoryHistory.setUserId(1L);
            }
            
            inventoryService.addInventoryHistory(inventoryHistory);
            
            String message = "入庫".equals(inventoryHistory.getType()) ? 
                    "入庫処理が完了しました" : "出庫処理が完了しました";
            redirectAttributes.addFlashAttribute("message", message);
            
            return "redirect:/inventory";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("products", productService.findAll());
            return "入庫".equals(inventoryHistory.getType()) ? "inventory/add" : "inventory/subtract";
        }
    }
    
    @PostMapping("/quick-process")
    @ResponseBody
    public String quickProcess(@RequestBody InventoryHistory inventoryHistory,
                              @AuthenticationPrincipal User user) {
        try {
            // Spring Securityのユーザー名からユーザーIDを取得
            // 簡易実装として管理者ユーザー（ID:1）を使用
            if (user != null && "admin".equals(user.getUsername())) {
                inventoryHistory.setUserId(1L);
            } else {
                // デフォルトは管理者ユーザー
                inventoryHistory.setUserId(1L);
            }
            
            inventoryService.addInventoryHistory(inventoryHistory);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // 定数メソッド
    private List<String> getAddReasons() {
        return Arrays.asList("仕入れ", "返品戻し", "在庫調整", "その他");
    }
    
    private List<String> getSubtractReasons() {
        return Arrays.asList("販売", "廃棄", "在庫調整", "その他");
    }
}