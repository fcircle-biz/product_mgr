package com.example.productmgr.service;

import com.example.productmgr.model.InventoryHistory;
import com.example.productmgr.model.Product;
import com.example.productmgr.repository.InventoryHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final ProductService productService;
    
    public List<InventoryHistory> findByProductId(Long productId) {
        return inventoryHistoryRepository.findByProductId(productId);
    }
    
    public List<InventoryHistory> findLatestByProductId(Long productId, int limit) {
        return inventoryHistoryRepository.findLatestByProductId(productId, limit);
    }
    
    public List<InventoryHistory> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return inventoryHistoryRepository.findByDateRange(startDate, endDate);
    }
    
    public List<InventoryHistory> findByType(String type) {
        return inventoryHistoryRepository.findByType(type);
    }
    
    public Optional<InventoryHistory> findById(Long id) {
        return inventoryHistoryRepository.findById(id);
    }
    
    @Transactional
    public InventoryHistory addInventoryHistory(InventoryHistory history) {
        // 商品の存在確認
        Optional<Product> productOpt = productService.findById(history.getProductId());
        if (productOpt.isEmpty()) {
            throw new RuntimeException("指定された商品が存在しません");
        }
        
        Product product = productOpt.get();
        
        // 互換性のために新しいフィールドにデータをセット
        // quantityChangeがセットされていない場合、typeとquantityから設定
        if (history.getQuantityChange() == null) {
            String type = history.getType();
            Integer quantity = history.getQuantity();
            
            // 数量がプラスであること確認
            if (quantity == null || quantity <= 0) {
                throw new RuntimeException("数量は1以上で指定してください");
            }
            
            history.setTypeAndQuantity(type, quantity);
        }
        
        // 出庫処理で在庫が不足していないか確認
        if (history.getQuantityChange() < 0 && product.getStockQuantity() < Math.abs(history.getQuantityChange())) {
            throw new RuntimeException("在庫数が不足しています");
        }
        
        // 旧フィールド名から新フィールド名への移行
        if (history.getOperatedBy() == null && history.getUserId() != null) {
            history.setOperatedBy(history.getUserId());
        }
        
        // 作成日時設定
        history.setCreatedAt(LocalDateTime.now());
        
        // 履歴保存（トリガーで在庫数が自動更新される）
        return inventoryHistoryRepository.save(history);
    }
    
    @Transactional
    public void deleteById(Long id) {
        inventoryHistoryRepository.deleteById(id);
    }
}