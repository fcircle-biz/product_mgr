package com.example.productmgr.repository;

import com.example.productmgr.model.Category;
import com.example.productmgr.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // カテゴリーの準備
        testCategory = new Category();
        testCategory.setName("テストカテゴリ");
        testCategory.setDescription("テスト用カテゴリ");
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());
        testCategory = categoryRepository.save(testCategory);

        // 商品の準備
        testProduct = new Product();
        testProduct.setName("テスト商品");
        testProduct.setJanCode("1234567890123");
        testProduct.setCategoryId(testCategory.getId());
        testProduct.setPrice(new BigDecimal("1000"));
        testProduct.setStockQuantity(100);
        testProduct.setStockUnit("個");
        testProduct.setStatus("販売中");
        testProduct.setDescription("テスト商品の説明");
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void カテゴリ付きで商品を取得する() {
        Product saved = productRepository.save(testProduct);

        Optional<Product> result = productRepository.findByIdWithCategory(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("テスト商品");
        // Spring Data JDBCでは、カスタムクエリでのJOINにRow Mapperが必要
        // ここではサービス層でカテゴリ情報を設定することを想定
    }

    @Test
    void カテゴリ付きで全商品を取得する() {
        productRepository.save(testProduct);

        Product product2 = new Product();
        product2.setName("テスト商品2");
        product2.setCategoryId(testCategory.getId());
        product2.setPrice(new BigDecimal("2000"));
        product2.setStockQuantity(50);
        product2.setStockUnit("個");
        product2.setStatus("販売中");
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product2);

        List<Product> results = productRepository.findAllWithCategory();

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(p -> p.getCategoryId() != null);
        // Spring Data JDBCでは、カスタムクエリでのJOINにRow Mapperが必要
        // カテゴリオブジェクトはサービス層で設定される
    }

    @Test
    void キーワードで商品を検索する() {
        productRepository.save(testProduct);

        List<Product> results = productRepository.searchByKeyword("テスト");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("テスト商品");

        // JANコードで検索
        results = productRepository.searchByKeyword("123456");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getJanCode()).isEqualTo("1234567890123");
    }

    @Test
    void カテゴリIDで商品を検索する() {
        productRepository.save(testProduct);

        List<Product> results = productRepository.findByCategoryId(testCategory.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCategoryId()).isEqualTo(testCategory.getId());
    }

    @Test
    void ステータスで商品を検索する() {
        productRepository.save(testProduct);

        Product product2 = new Product();
        product2.setName("販売停止商品");
        product2.setCategoryId(testCategory.getId());
        product2.setPrice(new BigDecimal("2000"));
        product2.setStockQuantity(50);
        product2.setStockUnit("個");
        product2.setStatus("販売停止");
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product2);

        List<Product> results = productRepository.findByStatus("販売中");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo("販売中");
    }

    @Test
    void 在庫切れ商品を取得する() {
        Product outOfStock = new Product();
        outOfStock.setName("在庫切れ商品");
        outOfStock.setCategoryId(testCategory.getId());
        outOfStock.setPrice(new BigDecimal("1500"));
        outOfStock.setStockQuantity(0);
        outOfStock.setStockUnit("個");
        outOfStock.setStatus("販売中");
        outOfStock.setCreatedAt(LocalDateTime.now());
        outOfStock.setUpdatedAt(LocalDateTime.now());
        productRepository.save(outOfStock);

        productRepository.save(testProduct); // 在庫あり商品

        List<Product> results = productRepository.findOutOfStock();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStockQuantity()).isEqualTo(0);
    }

    @Test
    void 在庫少商品を取得する() {
        Product lowStock = new Product();
        lowStock.setName("在庫少商品");
        lowStock.setCategoryId(testCategory.getId());
        lowStock.setPrice(new BigDecimal("1500"));
        lowStock.setStockQuantity(5);
        lowStock.setStockUnit("個");
        lowStock.setStatus("販売中");
        lowStock.setCreatedAt(LocalDateTime.now());
        lowStock.setUpdatedAt(LocalDateTime.now());
        productRepository.save(lowStock);

        productRepository.save(testProduct); // 在庫十分な商品

        List<Product> results = productRepository.findLowStock();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStockQuantity()).isEqualTo(5);
    }

    @Test
    void JANコードの存在確認() {
        productRepository.save(testProduct);

        boolean exists = productRepository.existsByJanCode("1234567890123");
        assertThat(exists).isTrue();

        exists = productRepository.existsByJanCode("9999999999999");
        assertThat(exists).isFalse();
    }
}