package com.example.productmgr.controller;

import com.example.productmgr.config.DataJdbcTestConfig;
import com.example.productmgr.config.TestControllerAdvice;
import com.example.productmgr.model.Category;
import com.example.productmgr.model.InventoryHistory;
import com.example.productmgr.model.Product;
import com.example.productmgr.repository.CategoryRepository;
import com.example.productmgr.repository.InventoryHistoryRepository;
import com.example.productmgr.repository.ProductRepository;
import com.example.productmgr.repository.SystemSettingRepository;
import com.example.productmgr.repository.UserRepository;
import com.example.productmgr.service.CategoryService;
import com.example.productmgr.service.InventoryService;
import com.example.productmgr.service.ProductService;
import com.example.productmgr.service.SystemSettingService;
import com.example.productmgr.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@Import({DataJdbcTestConfig.class, TestControllerAdvice.class})
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private InventoryHistoryRepository inventoryHistoryRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SystemSettingRepository systemSettingRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private SystemSettingService systemSettingService;

    private Product testProduct;
    private Category testCategory;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        // カテゴリーの準備
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("電化製品");
        testCategory.setDescription("家電・電子機器類");

        // 商品の準備
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("テスト商品");
        testProduct.setJanCode("1234567890123");
        testProduct.setCategoryId(1L);
        testProduct.setCategory(testCategory);
        testProduct.setPrice(new BigDecimal("1000"));
        testProduct.setStockQuantity(100);
        testProduct.setStockUnit("個");
        testProduct.setStatus("販売中");
        testProduct.setDescription("テスト商品の説明");
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        productList = Arrays.asList(testProduct);
    }

    @Test
    void 商品一覧を表示する() throws Exception {
        when(productService.findAll()).thenReturn(productList);
        when(categoryService.findAll()).thenReturn(Arrays.asList(testCategory));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void キーワードで商品を検索する() throws Exception {
        when(productService.searchByKeyword("テスト")).thenReturn(productList);
        when(categoryService.findAll()).thenReturn(Arrays.asList(testCategory));

        mockMvc.perform(get("/products")
                .param("keyword", "テスト"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchKeyword", "テスト"));
    }

    @Test
    void カテゴリーで商品を絞り込む() throws Exception {
        when(productService.findByCategoryId(1L)).thenReturn(productList);
        when(categoryService.findAll()).thenReturn(Arrays.asList(testCategory));

        mockMvc.perform(get("/products")
                .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("selectedCategoryId", 1L));
    }

    @Test
    void 商品詳細を表示する() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("product/detail"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void 存在しない商品詳細を表示しようとすると404エラー() throws Exception {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void 商品登録フォームを表示する() throws Exception {
        when(categoryService.findAllSorted()).thenReturn(Arrays.asList(testCategory));

        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/create"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void 新規商品を登録する() throws Exception {
        when(productService.save(any(Product.class))).thenReturn(testProduct);
        when(inventoryService.addInventoryHistory(any(InventoryHistory.class))).thenReturn(new InventoryHistory());

        mockMvc.perform(post("/products")
                .with(csrf())
                .param("name", "新規商品")
                .param("janCode", "9876543210123")
                .param("categoryId", "1")
                .param("price", "1500")
                .param("stockQuantity", "0")  // 初期在庫0に設定
                .param("stockUnit", "個"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/1"));
    }

    @Test
    void 商品編集フォームを表示する() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryService.findAllSorted()).thenReturn(Arrays.asList(testCategory));

        mockMvc.perform(get("/products/{id}/edit", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("product/edit"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void 商品情報を更新する() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productService.save(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(post("/products/{id}", 1L)
                .with(csrf())
                .param("id", "1")
                .param("name", "更新された商品")
                .param("janCode", "9876543210123")
                .param("categoryId", "1")
                .param("price", "2000")
                .param("stockUnit", "個"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void 商品を削除する() throws Exception {
        when(productService.existsById(1L)).thenReturn(true);

        mockMvc.perform(post("/products/{id}/delete", 1L)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void 一般ユーザーも商品を削除できる() throws Exception {
        when(productService.existsById(1L)).thenReturn(true);
        
        mockMvc.perform(post("/products/{id}/delete", 1L)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }
}