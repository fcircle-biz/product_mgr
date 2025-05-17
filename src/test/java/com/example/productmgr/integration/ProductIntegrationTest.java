package com.example.productmgr.integration;

import com.example.productmgr.model.Category;
import com.example.productmgr.model.Product;
import com.example.productmgr.repository.CategoryRepository;
import com.example.productmgr.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        // カテゴリーの作成
        testCategory = new Category();
        testCategory.setName("統合テストカテゴリ");
        testCategory.setDescription("統合テスト用カテゴリ");
        testCategory.setCreatedAt(java.time.LocalDateTime.now());
        testCategory.setUpdatedAt(java.time.LocalDateTime.now());
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    void 商品の作成から閲覧までのフロー() throws Exception {
        // 商品作成フォームを表示
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/create"))
                .andExpect(model().attributeExists("categories"));

        // 新規商品を作成
        mockMvc.perform(post("/products")
                .with(csrf())
                .param("name", "統合テスト商品")
                .param("janCode", "9876543210123")
                .param("categoryId", String.valueOf(testCategory.getId()))
                .param("price", "1500")
                .param("stockQuantity", "0")
                .param("stockUnit", "個")
                .param("status", "販売中")
                .param("description", "統合テスト用の商品です"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/products/*"));

        // 商品が正しく保存されたか確認
        Iterable<Product> products = productRepository.findAll();
        Product savedProduct = null;
        for (Product p : products) {
            if ("統合テスト商品".equals(p.getName())) {
                savedProduct = p;
                break;
            }
        }
        assert savedProduct != null;
        assert savedProduct.getJanCode().equals("9876543210123");

        // 商品詳細を表示
        mockMvc.perform(get("/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("product/detail"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    void 商品検索機能のテスト() throws Exception {
        // テスト用商品を作成
        Product product1 = new Product();
        product1.setName("検索テスト商品1");
        product1.setJanCode("1111111111111");
        product1.setCategoryId(testCategory.getId());
        product1.setPrice(new BigDecimal("1000"));
        product1.setStockQuantity(100);
        product1.setStockUnit("個");
        product1.setStatus("販売中");
        product1.setCreatedAt(java.time.LocalDateTime.now());
        product1.setUpdatedAt(java.time.LocalDateTime.now());
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("検索テスト商品2");
        product2.setJanCode("2222222222222");
        product2.setCategoryId(testCategory.getId());
        product2.setPrice(new BigDecimal("2000"));
        product2.setStockQuantity(0);
        product2.setStockUnit("個");
        product2.setStatus("販売中");
        product2.setCreatedAt(java.time.LocalDateTime.now());
        product2.setUpdatedAt(java.time.LocalDateTime.now());
        productRepository.save(product2);

        // キーワード検索
        mockMvc.perform(get("/products")
                .param("keyword", "商品1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list"))
                .andExpect(model().attributeExists("products"));

        // カテゴリー検索
        mockMvc.perform(get("/products")
                .param("categoryId", String.valueOf(testCategory.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list"))
                .andExpect(model().attributeExists("products"));

        // ステータス検索
        mockMvc.perform(get("/products")
                .param("status", "販売中"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @Transactional
    void 一般ユーザーも商品作成画面にアクセス可能() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk());

        // POSTは権限がなくても実行可能（実装による）
        mockMvc.perform(post("/products")
                .with(csrf())
                .param("name", "テスト商品")
                .param("janCode", "1234567890123")  // 必須フィールド追加
                .param("categoryId", String.valueOf(testCategory.getId()))
                .param("price", "1000")
                .param("stockQuantity", "0")
                .param("stockUnit", "個")
                .param("status", "販売中"))  // 必須フィールド追加
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Transactional
    void 未認証ユーザーは商品一覧にアクセスできない() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}