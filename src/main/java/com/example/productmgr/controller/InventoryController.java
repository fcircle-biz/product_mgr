package com.example.productmgr.controller;

import com.example.productmgr.model.InventoryHistory;
import com.example.productmgr.model.Product;
import com.example.productmgr.service.InventoryService;
import com.example.productmgr.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class InventoryController {

    private final ProductService productService;
    private final InventoryService inventoryService;
    
    @GetMapping
    public String listInventory(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) String stockStatus,
                                Model model) {
        log.info("listInventory method called with params: keyword={}, categoryId={}, stockStatus={}", 
                keyword, categoryId, stockStatus);
                
        List<Product> products;
        
        try {
            if (keyword != null && !keyword.isEmpty()) {
                log.info("Searching products by keyword: {}", keyword);
                products = productService.searchByKeyword(keyword);
                model.addAttribute("searchKeyword", keyword);
            } else if (categoryId != null) {
                log.info("Finding products by categoryId: {}", categoryId);
                products = productService.findByCategoryId(categoryId);
                model.addAttribute("selectedCategoryId", categoryId);
            } else if ("out_of_stock".equals(stockStatus)) {
                log.info("Finding out of stock products");
                products = productService.findOutOfStock();
                model.addAttribute("selectedStockStatus", stockStatus);
            } else if ("low_stock".equals(stockStatus)) {
                log.info("Finding low stock products");
                products = productService.findLowStock();
                model.addAttribute("selectedStockStatus", stockStatus);
            } else {
                log.info("Finding all products");
                products = productService.findAll();
            }
            
            log.info("Found {} products", products.size());
            model.addAttribute("products", products);
            log.info("Model populated with products attribute");
            
            return "inventory/list";
        } catch (Exception e) {
            log.error("Error in listInventory method", e);
            model.addAttribute("errorMessage", "在庫一覧の取得中にエラーが発生しました: " + e.getMessage());
            model.addAttribute("products", List.of());
            return "inventory/list";
        }
    }
    
    @GetMapping("/debug-info")
    @ResponseBody
    public String debugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("==== Inventory Debug Info ===\n");
        
        try {
            int totalCount = productService.getRepository().countAllProducts();
            info.append("Direct count query result: ").append(totalCount).append("\n");
            
            List<Product> products = productService.findAll();
            info.append("Total products: ").append(products.size()).append("\n");
            
            // データベース接続情報を取得
            info.append("\nDatabase Connection Info:\n");
            info.append("Active profile: ").append(System.getProperty("spring.profiles.active", "default")).append("\n");
            
            // 最初の5つの商品情報を表示
            info.append("\nFirst 5 products:\n");
            products.stream().limit(5).forEach(p -> {
                info.append(String.format("ID: %d, Name: %s, Stock: %d %s\n", 
                        p.getId(), p.getName(), p.getStockQuantity(), p.getStockUnit()));
            });
            
            // カテゴリ情報も確認
            info.append("\nCategory Info:\n");
            products.stream().limit(5).forEach(p -> {
                if (p.getCategory() != null) {
                    info.append(String.format("Product ID: %d, Category ID: %d, Category Name: %s\n", 
                            p.getId(), p.getCategory().getId(), p.getCategory().getName()));
                } else {
                    info.append(String.format("Product ID: %d, Category: null\n", p.getId()));
                }
            });
            
            return info.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage() + "\n" + e.getStackTrace()[0] + "\n" + e.getStackTrace()[1];
        }
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