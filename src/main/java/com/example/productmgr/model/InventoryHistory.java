package com.example.productmgr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("inventory_history")
public class InventoryHistory {

    @Id
    private Long id;
    
    @Column("product_id")
    private Long productId;
    
    private String type;  // 入庫/出庫
    
    private Integer quantity;
    
    private String reason;
    
    private String note;
    
    @Column("user_id")
    private Long userId;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    // 関連エンティティは別途結合で取得
    private Product product;
    private User user;
}