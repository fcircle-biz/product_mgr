package com.example.productmgr.service;

import com.example.productmgr.model.Product;
import com.example.productmgr.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    
    public List<Product> findAll() {
        return productRepository.findAllWithCategory();
    }
    
    public Optional<Product> findById(Long id) {
        return productRepository.findByIdWithCategory(id);
    }
    
    public List<Product> searchByKeyword(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }
    
    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    public List<Product> findByStatus(String status) {
        return productRepository.findByStatus(status);
    }
    
    public List<Product> findOutOfStock() {
        return productRepository.findOutOfStock();
    }
    
    public List<Product> findLowStock() {
        return productRepository.findLowStock();
    }
    
    @Transactional
    public Product save(Product product) {
        // カテゴリの存在確認
        if (!categoryService.existsById(product.getCategoryId())) {
            throw new RuntimeException("指定されたカテゴリが存在しません");
        }
        
        if (product.getId() == null) {
            // 新規商品登録
            if (product.getJanCode() != null && !product.getJanCode().isEmpty() 
                    && productRepository.existsByJanCode(product.getJanCode())) {
                throw new RuntimeException("このJANコードは既に登録されています");
            }
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
        } else {
            // 既存商品の更新
            Product existingProduct = productRepository.findById(product.getId())
                    .orElseThrow(() -> new RuntimeException("更新対象の商品が見つかりません"));
            
            // JANコードが変更されていて、それが他の商品で使用されている場合はエラー
            if (product.getJanCode() != null && !product.getJanCode().isEmpty() 
                    && !product.getJanCode().equals(existingProduct.getJanCode())
                    && productRepository.existsByJanCode(product.getJanCode())) {
                throw new RuntimeException("このJANコードは既に登録されています");
            }
            
            product.setCreatedAt(existingProduct.getCreatedAt());
            product.setStockQuantity(existingProduct.getStockQuantity()); // 在庫数量を保持
            product.setUpdatedAt(LocalDateTime.now());
            
        }
        
        Product savedProduct = productRepository.save(product);
        
        
        return savedProduct;
    }
    
    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }
    
    public ProductRepository getRepository() {
        return productRepository;
    }
}