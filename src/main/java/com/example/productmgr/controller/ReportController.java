package com.example.productmgr.controller;

import com.example.productmgr.model.InventoryHistory;
import com.example.productmgr.model.Product;
import com.example.productmgr.service.CategoryService;
import com.example.productmgr.service.InventoryService;
import com.example.productmgr.service.ProductService;
import com.example.productmgr.service.SystemSettingService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final InventoryService inventoryService;
    private final SystemSettingService systemSettingService;
    
    @GetMapping
    public String reportDashboard(Model model) {
        // 在庫状況サマリー
        List<Product> allProducts = productService.findAll();
        List<Product> outOfStockProducts = productService.findOutOfStock();
        List<Product> lowStockProducts = productService.findLowStock();
        
        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("outOfStockCount", outOfStockProducts.size());
        model.addAttribute("lowStockCount", lowStockProducts.size());
        
        // 今日の入出庫数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<InventoryHistory> todayHistories = inventoryService.findByDateRange(todayStart, todayEnd);
        
        int todayInbound = todayHistories.stream()
                .filter(h -> "入庫".equals(h.getType()))
                .mapToInt(InventoryHistory::getQuantity)
                .sum();
        
        int todayOutbound = todayHistories.stream()
                .filter(h -> "出庫".equals(h.getType()))
                .mapToInt(InventoryHistory::getQuantity)
                .sum();
        
        model.addAttribute("todayInbound", todayInbound);
        model.addAttribute("todayOutbound", todayOutbound);
        
        // 過去7日間の入出庫推移データ
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(6), LocalTime.MIN);
        List<InventoryHistory> weekHistories = inventoryService.findByDateRange(weekStart, todayEnd);
        
        // 過去7日間の日付リスト
        List<LocalDate> lastWeekDates = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            lastWeekDates.add(LocalDate.now().minusDays(i));
        }
        
        // 入庫データの集計
        Map<LocalDate, Integer> inboundByDate = new HashMap<>();
        // 出庫データの集計
        Map<LocalDate, Integer> outboundByDate = new HashMap<>();
        
        // 初期化
        lastWeekDates.forEach(date -> {
            inboundByDate.put(date, 0);
            outboundByDate.put(date, 0);
        });
        
        // データの集計
        weekHistories.forEach(history -> {
            LocalDate historyDate = history.getCreatedAt().toLocalDate();
            if (lastWeekDates.contains(historyDate)) {
                if ("入庫".equals(history.getType())) {
                    inboundByDate.put(historyDate, inboundByDate.get(historyDate) + history.getQuantity());
                } else if ("出庫".equals(history.getType())) {
                    outboundByDate.put(historyDate, outboundByDate.get(historyDate) + history.getQuantity());
                }
            }
        });
        
        // 日付をString形式に変換
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        List<String> dateLabels = lastWeekDates.stream()
                .map(formatter::format)
                .collect(Collectors.toList());
        
        // 入出庫データの配列を作成
        List<Integer> inboundData = lastWeekDates.stream()
                .map(inboundByDate::get)
                .collect(Collectors.toList());
        
        List<Integer> outboundData = lastWeekDates.stream()
                .map(outboundByDate::get)
                .collect(Collectors.toList());
        
        model.addAttribute("dateLabels", dateLabels);
        model.addAttribute("inboundData", inboundData);
        model.addAttribute("outboundData", outboundData);
        
        // カテゴリ別商品数
        model.addAttribute("categories", categoryService.findAllSorted());
        
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
    
    @GetMapping("/daily")
    public String dailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        
        // デフォルトは今日
        if (date == null) {
            date = LocalDate.now();
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
        
        // 指定された日の在庫履歴を取得
        List<InventoryHistory> histories = inventoryService.findByDateRange(startDateTime, endDateTime);
        
        // 入庫履歴
        List<InventoryHistory> inboundHistories = histories.stream()
                .filter(h -> "入庫".equals(h.getType()))
                .toList();
        
        // 出庫履歴
        List<InventoryHistory> outboundHistories = histories.stream()
                .filter(h -> "出庫".equals(h.getType()))
                .toList();
        
        // 入庫合計
        int totalInboundCount = inboundHistories.stream().mapToInt(InventoryHistory::getQuantity).sum();
        
        // 出庫合計
        int totalOutboundCount = outboundHistories.stream().mapToInt(InventoryHistory::getQuantity).sum();
        
        // 入庫理由別の集計
        Map<String, Integer> inboundByReason = inboundHistories.stream()
                .collect(Collectors.groupingBy(
                        InventoryHistory::getReason,
                        Collectors.summingInt(InventoryHistory::getQuantity)
                ));
        
        // 出庫理由別の集計
        Map<String, Integer> outboundByReason = outboundHistories.stream()
                .collect(Collectors.groupingBy(
                        InventoryHistory::getReason,
                        Collectors.summingInt(InventoryHistory::getQuantity)
                ));
        
        // 商品別の集計
        Map<Long, Integer> inboundByProduct = inboundHistories.stream()
                .collect(Collectors.groupingBy(
                        InventoryHistory::getProductId,
                        Collectors.summingInt(InventoryHistory::getQuantity)
                ));
        
        Map<Long, Integer> outboundByProduct = outboundHistories.stream()
                .collect(Collectors.groupingBy(
                        InventoryHistory::getProductId,
                        Collectors.summingInt(InventoryHistory::getQuantity)
                ));
        
        // 商品情報のマッピング
        Map<Long, Product> productMap = productService.findAll().stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        
        model.addAttribute("date", date);
        model.addAttribute("inboundHistories", inboundHistories);
        model.addAttribute("outboundHistories", outboundHistories);
        model.addAttribute("totalInboundCount", totalInboundCount);
        model.addAttribute("totalOutboundCount", totalOutboundCount);
        model.addAttribute("inboundByReason", inboundByReason);
        model.addAttribute("outboundByReason", outboundByReason);
        model.addAttribute("inboundByProduct", inboundByProduct);
        model.addAttribute("outboundByProduct", outboundByProduct);
        model.addAttribute("productMap", productMap);
        
        return "report/daily";
    }
    
    @GetMapping("/monthly")
    public String monthlyReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") String yearMonth,
            Model model) {
        
        // デフォルトは今月
        YearMonth targetMonth;
        if (yearMonth == null) {
            targetMonth = YearMonth.now();
        } else {
            targetMonth = YearMonth.parse(yearMonth);
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(targetMonth.atDay(1), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(targetMonth.atEndOfMonth(), LocalTime.MAX);
        
        // 指定された月の在庫履歴を取得
        List<InventoryHistory> histories = inventoryService.findByDateRange(startDateTime, endDateTime);
        
        // 入庫履歴
        List<InventoryHistory> inboundHistories = histories.stream()
                .filter(h -> "入庫".equals(h.getType()))
                .toList();
        
        // 出庫履歴
        List<InventoryHistory> outboundHistories = histories.stream()
                .filter(h -> "出庫".equals(h.getType()))
                .toList();
        
        // 入庫合計
        int totalInboundCount = inboundHistories.stream().mapToInt(InventoryHistory::getQuantity).sum();
        
        // 出庫合計
        int totalOutboundCount = outboundHistories.stream().mapToInt(InventoryHistory::getQuantity).sum();
        
        // 日別集計データの作成
        Map<Integer, Integer> inboundByDay = new HashMap<>();
        Map<Integer, Integer> outboundByDay = new HashMap<>();
        
        // 初期化
        for (int i = 1; i <= targetMonth.lengthOfMonth(); i++) {
            inboundByDay.put(i, 0);
            outboundByDay.put(i, 0);
        }
        
        // データの集計
        inboundHistories.forEach(history -> {
            int day = history.getCreatedAt().getDayOfMonth();
            inboundByDay.put(day, inboundByDay.get(day) + history.getQuantity());
        });
        
        outboundHistories.forEach(history -> {
            int day = history.getCreatedAt().getDayOfMonth();
            outboundByDay.put(day, outboundByDay.get(day) + history.getQuantity());
        });
        
        // 理由別集計
        Map<String, Integer> inboundByReason = inboundHistories.stream()
                .collect(Collectors.groupingBy(
                        InventoryHistory::getReason,
                        Collectors.summingInt(InventoryHistory::getQuantity)
                ));
        
        Map<String, Integer> outboundByReason = outboundHistories.stream()
                .collect(Collectors.groupingBy(
                        InventoryHistory::getReason,
                        Collectors.summingInt(InventoryHistory::getQuantity)
                ));
        
        // 商品別の集計
        Map<Long, Integer> inboundByProduct = inboundHistories.stream()
                .collect(Collectors.groupingBy(
                        InventoryHistory::getProductId,
                        Collectors.summingInt(InventoryHistory::getQuantity)
                ));
        
        Map<Long, Integer> outboundByProduct = outboundHistories.stream()
                .collect(Collectors.groupingBy(
                        InventoryHistory::getProductId,
                        Collectors.summingInt(InventoryHistory::getQuantity)
                ));
        
        // 商品情報のマッピング
        Map<Long, Product> productMap = productService.findAll().stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        
        model.addAttribute("yearMonth", targetMonth);
        model.addAttribute("daysInMonth", targetMonth.lengthOfMonth());
        model.addAttribute("inboundHistories", inboundHistories);
        model.addAttribute("outboundHistories", outboundHistories);
        model.addAttribute("totalInboundCount", totalInboundCount);
        model.addAttribute("totalOutboundCount", totalOutboundCount);
        model.addAttribute("inboundByDay", inboundByDay);
        model.addAttribute("outboundByDay", outboundByDay);
        model.addAttribute("inboundByReason", inboundByReason);
        model.addAttribute("outboundByReason", outboundByReason);
        model.addAttribute("inboundByProduct", inboundByProduct);
        model.addAttribute("outboundByProduct", outboundByProduct);
        model.addAttribute("productMap", productMap);
        
        return "report/monthly";
    }
    
    @GetMapping("/product/{id}")
    public String productReport(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        // 商品の存在確認
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
        
        // デフォルトは過去30日間
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        
        // 商品の在庫履歴を取得
        List<InventoryHistory> histories = inventoryService.findByProductId(id);
        
        // 期間内のデータにフィルタリング
        List<InventoryHistory> filteredHistories = histories.stream()
                .filter(h -> !h.getCreatedAt().isBefore(startDateTime) && !h.getCreatedAt().isAfter(endDateTime))
                .sorted(Comparator.comparing(InventoryHistory::getCreatedAt))
                .toList();
        
        // 在庫推移データの作成
        List<LocalDate> dates = new ArrayList<>();
        List<Integer> stockLevels = new ArrayList<>();
        
        // 開始時点の在庫数算出
        int initialStock = product.getStockQuantity();
        for (InventoryHistory history : histories) {
            if (history.getCreatedAt().isAfter(endDateTime)) {
                // 終了日以降のデータは無視
                continue;
            }
            
            if (history.getCreatedAt().isBefore(startDateTime)) {
                // 開始日より前のデータは初期在庫の計算に使用
                if ("入庫".equals(history.getType())) {
                    initialStock -= history.getQuantity();
                } else if ("出庫".equals(history.getType())) {
                    initialStock += history.getQuantity();
                }
            }
        }
        
        // 在庫がマイナスにならないよう調整
        if (initialStock < 0) {
            initialStock = 0;
        }
        
        // 各日の在庫推移を計算
        int currentStock = initialStock;
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            LocalDate finalCurrentDate = currentDate; // for lambda
            
            // 当日の履歴を取得して在庫を更新
            List<InventoryHistory> dayHistories = filteredHistories.stream()
                    .filter(h -> h.getCreatedAt().toLocalDate().equals(finalCurrentDate))
                    .sorted(Comparator.comparing(InventoryHistory::getCreatedAt))
                    .toList();
            
            for (InventoryHistory history : dayHistories) {
                if ("入庫".equals(history.getType())) {
                    currentStock += history.getQuantity();
                } else if ("出庫".equals(history.getType())) {
                    currentStock -= history.getQuantity();
                }
                
                // 在庫がマイナスにならないよう調整
                if (currentStock < 0) {
                    currentStock = 0;
                }
            }
            
            dates.add(currentDate);
            stockLevels.add(currentStock);
            
            // 次の日へ
            currentDate = currentDate.plusDays(1);
        }
        
        // 日付をString形式に変換
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> dateLabels = dates.stream()
                .map(formatter::format)
                .collect(Collectors.toList());
        
        model.addAttribute("product", product);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("histories", filteredHistories);
        model.addAttribute("dateLabels", dateLabels);
        model.addAttribute("stockLevels", stockLevels);
        
        return "report/product";
    }
    
    @GetMapping("/export/daily")
    public void exportDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletResponse response) throws IOException {
        
        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
        
        // 指定された日の在庫履歴を取得
        List<InventoryHistory> histories = inventoryService.findByDateRange(startDateTime, endDateTime);
        
        // レスポンスの設定
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=daily_report_" + date + ".csv");
        
        // CSVヘッダー
        String[] headers = {"商品ID", "商品名", "取引種別", "数量", "理由", "備考", "日時"};
        
        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            
            // 商品情報のマッピング
            Map<Long, Product> productMap = productService.findAll().stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
            
            // データの書き込み
            for (InventoryHistory history : histories) {
                Product product = productMap.get(history.getProductId());
                String productName = product != null ? product.getName() : "Unknown";
                
                csvPrinter.printRecord(
                        history.getProductId(),
                        productName,
                        history.getType(),
                        history.getQuantity(),
                        history.getReason(),
                        history.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
            }
            
            csvPrinter.flush();
        }
    }
    
    @GetMapping("/export/monthly")
    public void exportMonthlyReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") String yearMonth,
            HttpServletResponse response) throws IOException {
        
        // デフォルトは今月
        YearMonth targetMonth;
        if (yearMonth == null) {
            targetMonth = YearMonth.now();
        } else {
            targetMonth = YearMonth.parse(yearMonth);
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(targetMonth.atDay(1), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(targetMonth.atEndOfMonth(), LocalTime.MAX);
        
        // 指定された月の在庫履歴を取得
        List<InventoryHistory> histories = inventoryService.findByDateRange(startDateTime, endDateTime);
        
        // レスポンスの設定
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=monthly_report_" + targetMonth + ".csv");
        
        // CSVヘッダー
        String[] headers = {"日付", "商品ID", "商品名", "取引種別", "数量", "理由", "備考"};
        
        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            
            // 商品情報のマッピング
            Map<Long, Product> productMap = productService.findAll().stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
            
            // データの書き込み
            for (InventoryHistory history : histories) {
                Product product = productMap.get(history.getProductId());
                String productName = product != null ? product.getName() : "Unknown";
                
                csvPrinter.printRecord(
                        history.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        history.getProductId(),
                        productName,
                        history.getType(),
                        history.getQuantity(),
                        history.getReason()
                );
            }
            
            csvPrinter.flush();
        }
    }
    
    @GetMapping("/export/stock-warning")
    public void exportStockWarningReport(HttpServletResponse response) throws IOException {
        List<Product> outOfStockProducts = productService.findOutOfStock();
        List<Product> lowStockProducts = productService.findLowStock();
        
        // レスポンスの設定
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=stock_warning_report.csv");
        
        // CSVヘッダー
        String[] headers = {"商品ID", "商品コード", "商品名", "カテゴリ", "現在の在庫", "警告状態", "価格"};
        
        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            
            // カテゴリ情報のマッピング
            Map<Long, String> categoryMap = categoryService.findAllSorted().stream()
                    .collect(Collectors.toMap(c -> c.getId(), c -> c.getName()));
            
            // 在庫切れ商品のデータ書き込み
            for (Product product : outOfStockProducts) {
                String categoryName = categoryMap.getOrDefault(product.getCategoryId(), "未分類");
                
                csvPrinter.printRecord(
                        product.getId(),
                        product.getJanCode(),
                        product.getName(),
                        categoryName,
                        product.getStockQuantity(),
                        "在庫切れ",
                        product.getPrice()
                );
            }
            
            // 在庫少商品のデータ書き込み
            for (Product product : lowStockProducts) {
                String categoryName = categoryMap.getOrDefault(product.getCategoryId(), "未分類");
                
                csvPrinter.printRecord(
                        product.getId(),
                        product.getJanCode(),
                        product.getName(),
                        categoryName,
                        product.getStockQuantity(),
                        "在庫少",
                        product.getPrice()
                );
            }
            
            csvPrinter.flush();
        }
    }
    
    @GetMapping("/export/category-distribution")
    public void exportCategoryDistribution(HttpServletResponse response) throws IOException {
        List<Product> products = productService.findAll();
        
        // レスポンスの設定
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=category_distribution.csv");
        
        // CSVヘッダー
        String[] headers = {"カテゴリID", "カテゴリ名", "商品数", "総在庫数", "平均価格"};
        
        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            
            // カテゴリ情報を取得
            List<Map<String, Object>> categoryStats = new ArrayList<>();
            
            // カテゴリごとの統計情報を計算
            categoryService.findAllSorted().forEach(category -> {
                Map<String, Object> stats = new HashMap<>();
                stats.put("categoryId", category.getId());
                stats.put("categoryName", category.getName());
                
                List<Product> categoryProducts = products.stream()
                        .filter(p -> p.getCategoryId().equals(category.getId()))
                        .collect(Collectors.toList());
                
                int productCount = categoryProducts.size();
                int totalStock = categoryProducts.stream()
                        .mapToInt(Product::getStockQuantity)
                        .sum();
                
                double averagePrice = categoryProducts.stream()
                        .mapToDouble(p -> p.getPrice().doubleValue())
                        .average()
                        .orElse(0.0);
                
                stats.put("productCount", productCount);
                stats.put("totalStock", totalStock);
                stats.put("averagePrice", averagePrice);
                
                categoryStats.add(stats);
            });
            
            // データの書き込み
            for (Map<String, Object> stats : categoryStats) {
                csvPrinter.printRecord(
                        stats.get("categoryId"),
                        stats.get("categoryName"),
                        stats.get("productCount"),
                        stats.get("totalStock"),
                        stats.get("averagePrice")
                );
            }
            
            csvPrinter.flush();
        }
    }
    
    @GetMapping("/inventory-turnover")
    public String inventoryTurnoverReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        // デフォルトは過去90日間（3ヶ月）
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(90);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        
        // 期間（日数）を計算
        long periodDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // 全商品リスト取得
        List<Product> products = productService.findAll();
        
        // 在庫回転率のデータを格納するリスト
        List<Map<String, Object>> turnoverData = new ArrayList<>();
        
        // 商品ごとに在庫回転率を計算
        for (Product product : products) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("product", product);
            
            // 商品のIDを取得
            Long productId = product.getId();
            
            // 商品の在庫履歴を取得
            List<InventoryHistory> productHistories = inventoryService.findByProductId(productId).stream()
                    .filter(h -> !h.getCreatedAt().isBefore(startDateTime) && !h.getCreatedAt().isAfter(endDateTime))
                    .collect(Collectors.toList());
            
            // 出庫数量の合計を計算
            int totalOutbound = productHistories.stream()
                    .filter(h -> "出庫".equals(h.getType()))
                    .mapToInt(InventoryHistory::getQuantity)
                    .sum();
            
            // 現在の在庫数量を取得
            int currentStock = product.getStockQuantity();
            
            // 初期在庫を計算
            int initialStock = currentStock;
            
            // 期間中の入出庫を考慮して初期在庫を算出
            for (InventoryHistory history : productHistories) {
                if ("入庫".equals(history.getType())) {
                    initialStock -= history.getQuantity();
                } else if ("出庫".equals(history.getType())) {
                    initialStock += history.getQuantity();
                }
            }
            
            // 初期在庫がマイナスにならないよう調整
            if (initialStock < 0) {
                initialStock = 0;
            }
            
            // 平均在庫を計算
            double averageStock = (initialStock + currentStock) / 2.0;
            
            // 在庫回転率 = 出庫数 / 平均在庫
            double turnoverRate = averageStock > 0 ? totalOutbound / averageStock : 0;
            
            // 年間換算の在庫回転率 = 回転率 * (365 / 期間日数)
            double annualizedTurnoverRate = turnoverRate * (365.0 / periodDays);
            
            // 在庫回転日数 = 平均在庫 / (出庫数 / 期間日数)
            double dailyOutbound = totalOutbound / (double) periodDays;
            double turnoverDays = dailyOutbound > 0 ? averageStock / dailyOutbound : 0;
            
            productData.put("initialStock", initialStock);
            productData.put("currentStock", currentStock);
            productData.put("averageStock", averageStock);
            productData.put("totalOutbound", totalOutbound);
            productData.put("turnoverRate", BigDecimal.valueOf(turnoverRate).setScale(2, RoundingMode.HALF_UP));
            productData.put("annualizedTurnoverRate", BigDecimal.valueOf(annualizedTurnoverRate).setScale(2, RoundingMode.HALF_UP));
            productData.put("turnoverDays", BigDecimal.valueOf(turnoverDays).setScale(1, RoundingMode.HALF_UP));
            
            turnoverData.add(productData);
        }
        
        // 在庫回転率の降順でソート
        turnoverData.sort((a, b) -> {
            BigDecimal rateA = (BigDecimal) a.get("annualizedTurnoverRate");
            BigDecimal rateB = (BigDecimal) b.get("annualizedTurnoverRate");
            return rateB.compareTo(rateA);
        });
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("turnoverData", turnoverData);
        model.addAttribute("totalProducts", products.size());
        
        return "report/inventory_turnover";
    }
    
    @GetMapping("/export/inventory-turnover")
    public void exportInventoryTurnoverReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {
        
        // デフォルトは過去90日間（3ヶ月）
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(90);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        
        // 期間（日数）を計算
        long periodDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // 全商品リスト取得
        List<Product> products = productService.findAll();
        
        // レスポンスの設定
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=inventory_turnover_" + startDate + "_to_" + endDate + ".csv");
        
        // CSVヘッダー
        String[] headers = {"商品ID", "商品コード", "商品名", "カテゴリ", "初期在庫", "現在在庫", "平均在庫", "出庫数", "在庫回転率", "年間換算回転率", "在庫回転日数"};
        
        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            
            // カテゴリ情報のマッピング
            Map<Long, String> categoryMap = categoryService.findAllSorted().stream()
                    .collect(Collectors.toMap(c -> c.getId(), c -> c.getName()));
            
            // 商品ごとに在庫回転率を計算
            for (Product product : products) {
                // 商品のIDを取得
                Long productId = product.getId();
                String categoryName = categoryMap.getOrDefault(product.getCategoryId(), "未分類");
                
                // 商品の在庫履歴を取得
                List<InventoryHistory> productHistories = inventoryService.findByProductId(productId).stream()
                        .filter(h -> !h.getCreatedAt().isBefore(startDateTime) && !h.getCreatedAt().isAfter(endDateTime))
                        .collect(Collectors.toList());
                
                // 出庫数量の合計を計算
                int totalOutbound = productHistories.stream()
                        .filter(h -> "出庫".equals(h.getType()))
                        .mapToInt(InventoryHistory::getQuantity)
                        .sum();
                
                // 現在の在庫数量を取得
                int currentStock = product.getStockQuantity();
                
                // 初期在庫を計算
                int initialStock = currentStock;
                
                // 期間中の入出庫を考慮して初期在庫を算出
                for (InventoryHistory history : productHistories) {
                    if ("入庫".equals(history.getType())) {
                        initialStock -= history.getQuantity();
                    } else if ("出庫".equals(history.getType())) {
                        initialStock += history.getQuantity();
                    }
                }
                
                // 初期在庫がマイナスにならないよう調整
                if (initialStock < 0) {
                    initialStock = 0;
                }
                
                // 平均在庫を計算
                double averageStock = (initialStock + currentStock) / 2.0;
                
                // 在庫回転率 = 出庫数 / 平均在庫
                double turnoverRate = averageStock > 0 ? totalOutbound / averageStock : 0;
                
                // 年間換算の在庫回転率 = 回転率 * (365 / 期間日数)
                double annualizedTurnoverRate = turnoverRate * (365.0 / periodDays);
                
                // 在庫回転日数 = 平均在庫 / (出庫数 / 期間日数)
                double dailyOutbound = totalOutbound / (double) periodDays;
                double turnoverDays = dailyOutbound > 0 ? averageStock / dailyOutbound : 0;
                
                csvPrinter.printRecord(
                        product.getId(),
                        product.getJanCode(),
                        product.getName(),
                        categoryName,
                        initialStock,
                        currentStock,
                        BigDecimal.valueOf(averageStock).setScale(1, RoundingMode.HALF_UP),
                        totalOutbound,
                        BigDecimal.valueOf(turnoverRate).setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(annualizedTurnoverRate).setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(turnoverDays).setScale(1, RoundingMode.HALF_UP)
                );
            }
            
            csvPrinter.flush();
        }
    }
}