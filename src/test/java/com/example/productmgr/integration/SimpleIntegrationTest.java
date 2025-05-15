package com.example.productmgr.integration;

import com.example.productmgr.model.Category;
import com.example.productmgr.model.Product;
import com.example.productmgr.repository.CategoryRepository;
import com.example.productmgr.repository.ProductRepository;
import com.example.productmgr.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimpleIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // カテゴリーを準備
        testCategory = new Category();
        testCategory.setName("統合テストカテゴリ");
        testCategory.setDescription("統合テスト用カテゴリ");
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void contextLoads() {
        assertThat(productService).isNotNull();
        assertThat(productRepository).isNotNull();
        assertThat(categoryRepository).isNotNull();
    }

    @Test
    void saveAndFindProduct() {
        // 新規商品を作成
        Product product = new Product();
        product.setName("統合テスト商品");
        product.setCategoryId(testCategory.getId());
        product.setPrice(new BigDecimal("1000"));
        product.setStockQuantity(100);
        product.setStockUnit("個");
        product.setStatus("販売中");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // 保存
        Product saved = productRepository.save(product);
        assertThat(saved.getId()).isNotNull();

        // 検索
        List<Product> products = productService.findAll();
        assertThat(products).isNotEmpty();
        assertThat(products.stream().anyMatch(p -> p.getName().equals("統合テスト商品"))).isTrue();
    }
}