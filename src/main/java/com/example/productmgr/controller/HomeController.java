package com.example.productmgr.controller;

import com.example.productmgr.model.Product;
import com.example.productmgr.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    
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
            
            return "main_menu";
        } else {
            return "redirect:/login";
        }
    }
}