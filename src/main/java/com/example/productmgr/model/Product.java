package com.example.productmgr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {

    @Id
    private Long id;
    
    private String name;
    
    @Column("jan_code")
    private String janCode;
    
    @Column("category_id")
    private Long categoryId;
    
    private BigDecimal price;
    
    @Column("stock_quantity")
    private Integer stockQuantity;
    
    @Column("stock_unit")
    private String stockUnit;
    
    private String status;
    
    private String description;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
    
    // カテゴリは別途結合して取得
    @org.springframework.data.annotation.Transient
    private Category category;
}