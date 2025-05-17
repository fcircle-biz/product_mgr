package com.example.productmgr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("inventory_histories")
public class InventoryHistory {

    @Id
    private Long id;
    
    @Column("product_id")
    private Long productId;
    
    @Column("quantity_change")
    private Integer quantityChange;
    
    private String reason;
    
    @Column("operated_by")
    private Long operatedBy;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    // 関連エンティティは別途結合で取得
    @Transient
    private Product product;
    
    @Transient
    private User user;
    
    // 元のフィールドとの互換性のためのメソッド
    @Transient
    public String getType() {
        if (quantityChange == null) {
            return "";
        }
        return quantityChange > 0 ? "入庫" : "出庫";
    }
    
    @Transient
    public void setType(String type) {
        // 新しいフィールドに直接設定するわけではないので、このメソッドは空実装
        // quantityとtypeの組み合わせはsetQuantityChangeで処理される
    }
    
    @Transient
    public Integer getQuantity() {
        if (quantityChange == null) {
            return 0;
        }
        return Math.abs(quantityChange);
    }
    
    @Transient
    public void setQuantity(Integer quantity) {
        // 入出庫タイプに基づいて値を設定
        if (quantity == null) {
            this.quantityChange = 0;
            return;
        }
        if (this.getType().equals("入庫")) {
            this.quantityChange = Math.abs(quantity);
        } else {
            this.quantityChange = -Math.abs(quantity);
        }
    }
    
    @Transient
    public Long getUserId() {
        return operatedBy;
    }
    
    @Transient
    public void setUserId(Long userId) {
        this.operatedBy = userId;
    }
    
    // 古いインターフェースを維持しながら新しいフィールドを設定するための便利メソッド
    public void setTypeAndQuantity(String type, Integer quantity) {
        if ("入庫".equals(type)) {
            this.quantityChange = Math.abs(quantity);
        } else if ("出庫".equals(type)) {
            this.quantityChange = -Math.abs(quantity);
        } else {
            throw new IllegalArgumentException("タイプは「入庫」または「出庫」のいずれかを指定してください");
        }
    }
    
    // descriptionとの互換性
    @Transient
    private String description;
    
    @Transient
    public String getDescription() {
        return this.reason;
    }
    
    @Transient
    public void setDescription(String description) {
        this.reason = description;
    }
    
    // noteプロパティ（UIとの互換性）
    @Transient
    private String note;
    
    @Transient
    public String getNote() {
        return this.note;
    }
    
    @Transient
    public void setNote(String note) {
        this.note = note;
    }
}