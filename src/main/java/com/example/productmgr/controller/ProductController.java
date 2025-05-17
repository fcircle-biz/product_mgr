package com.example.productmgr.controller;

import com.example.productmgr.model.Category;
import com.example.productmgr.model.InventoryHistory;
import com.example.productmgr.model.Product;
import com.example.productmgr.service.CategoryService;
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

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final InventoryService inventoryService;
    
    @GetMapping
    public String listProducts(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) String status,
                               Model model) {
        List<Product> products;
        
        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchByKeyword(keyword);
            model.addAttribute("searchKeyword", keyword);
        } else if (categoryId != null) {
            products = productService.findByCategoryId(categoryId);
            model.addAttribute("selectedCategoryId", categoryId);
        } else if (status != null && !status.isEmpty()) {
            products = productService.findByStatus(status);
            model.addAttribute("selectedStatus", status);
        } else {
            products = productService.findAll();
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAllSorted());
        
        return "product/list";
    }
    
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
        
        List<InventoryHistory> histories = inventoryService.findLatestByProductId(id, 5);
        
        model.addAttribute("product", product);
        model.addAttribute("inventoryHistories", histories);
        
        return "product/detail";
    }
    
    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAllSorted());
        return "product/create";
    }
    
    @PostMapping
    public String createProduct(@Valid Product product, 
                                BindingResult result, 
                                @AuthenticationPrincipal User user,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllSorted());
            return "product/create";
        }
        
        try {
            Product savedProduct = productService.save(product);
            
            // 初期在庫登録
            if (product.getStockQuantity() > 0) {
                InventoryHistory history = new InventoryHistory();
                history.setProductId(savedProduct.getId());
                history.setType("入庫");
                history.setQuantity(product.getStockQuantity());
                history.setReason("初期在庫（初期登録）");
                // TODO: 実際のユーザーIDを設定
                history.setUserId(1L);
                
                inventoryService.addInventoryHistory(history);
            }
            
            redirectAttributes.addFlashAttribute("message", "商品を登録しました");
            return "redirect:/products/" + savedProduct.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.findAllSorted());
            return "product/create";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAllSorted());
        
        return "product/edit";
    }
    
    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid Product product,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllSorted());
            return "product/edit";
        }
        
        try {
            product.setId(id);
            productService.save(product);
            redirectAttributes.addFlashAttribute("message", "商品情報を更新しました");
            return "redirect:/products/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.findAllSorted());
            return "product/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "商品を削除しました");
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products/" + id;
        }
    }
}