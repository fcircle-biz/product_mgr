package com.example.productmgr.controller;

import com.example.productmgr.model.InventoryHistory;
import com.example.productmgr.model.Product;
import com.example.productmgr.service.CategoryService;
import com.example.productmgr.service.InventoryService;
import com.example.productmgr.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final InventoryService inventoryService;
    
    @GetMapping
    public String reportDashboard(Model model) {
        // 在庫状況サマリー
        List<Product> allProducts = productService.findAll();
        List<Product> outOfStockProducts = productService.findOutOfStock();
        List<Product> lowStockProducts = productService.findLowStock();
        
        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("outOfStockCount", outOfStockProducts.size());
        model.addAttribute("lowStockCount", lowStockProducts.size());
        
        return "report/dashboard";
    }
    
    @GetMapping("/stock-warning")
    public String stockWarningReport(Model model) {
        List<Product> outOfStockProducts = productService.findOutOfStock();
        List<Product> lowStockProducts = productService.findLowStock();
        
        model.addAttribute("outOfStockProducts", outOfStockProducts);
        model.addAttribute("lowStockProducts", lowStockProducts);
        
        return "report/stock_warning";
    }
    
    @GetMapping("/inventory-summary")
    public String inventorySummaryReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        // デフォルトは過去30日間
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        
        List<InventoryHistory> histories = inventoryService.findByDateRange(startDateTime, endDateTime);
        
        // 入庫履歴
        List<InventoryHistory> inboundHistories = histories.stream()
                .filter(h -> "入庫".equals(h.getType()))
                .toList();
        
        // 出庫履歴
        List<InventoryHistory> outboundHistories = histories.stream()
                .filter(h -> "出庫".equals(h.getType()))
                .toList();
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("inboundHistories", inboundHistories);
        model.addAttribute("outboundHistories", outboundHistories);
        model.addAttribute("totalInboundCount", inboundHistories.stream().mapToInt(InventoryHistory::getQuantity).sum());
        model.addAttribute("totalOutboundCount", outboundHistories.stream().mapToInt(InventoryHistory::getQuantity).sum());
        
        return "report/inventory_summary";
    }
    
    @GetMapping("/category-distribution")
    public String categoryDistributionReport(Model model) {
        model.addAttribute("categories", categoryService.findAllSorted());
        model.addAttribute("products", productService.findAll());
        
        return "report/category_distribution";
    }
}