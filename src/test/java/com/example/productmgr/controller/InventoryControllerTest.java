package com.example.productmgr.controller;

import com.example.productmgr.config.DataJdbcTestConfig;
import com.example.productmgr.config.TestControllerAdvice;
import com.example.productmgr.model.Category;
import com.example.productmgr.model.InventoryHistory;
import com.example.productmgr.model.Product;
import com.example.productmgr.model.User;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import({DataJdbcTestConfig.class, TestControllerAdvice.class})
@WithMockUser(username = "testuser", roles = {"USER"})
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private UserService userService;
    
    @MockBean
    private CategoryService categoryService;
    
    @MockBean
    private SystemSettingService systemSettingService;
    
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

    private Product testProduct;
    private User testUser;
    private InventoryHistory testHistory;

    @BeforeEach
    void setUp() {
        // カテゴリーの準備
        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("テストカテゴリ");

        // 商品の準備
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("テスト商品");
        testProduct.setCategoryId(1L);
        testProduct.setCategory(testCategory);
        testProduct.setPrice(new BigDecimal("1000"));
        testProduct.setStockQuantity(100);
        testProduct.setStockUnit("個");
        testProduct.setStatus("販売中");

        // ユーザーの準備
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("テストユーザー");

        // 在庫履歴の準備
        testHistory = new InventoryHistory();
        testHistory.setId(1L);
        testHistory.setProductId(1L);
        testHistory.setProduct(testProduct);
        testHistory.setQuantityChange(50);  // 入庫なのでプラス
        testHistory.setReason("通常入荷");
        testHistory.setOperatedBy(1L);
        testHistory.setUser(testUser);
        testHistory.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void 在庫一覧を表示する() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventory/list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void 入庫フォームを表示する() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/inventory/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventory/add"))
                .andExpect(model().attributeExists("inventoryHistory"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("type"));
    }

    @Test
    void 入庫処理を実行する() throws Exception {
        when(inventoryService.addInventoryHistory(any(InventoryHistory.class)))
                .thenReturn(testHistory);

        mockMvc.perform(post("/inventory/process")
                .with(csrf())
                .param("productId", "1")
                .param("quantityChange", "50")
                .param("type", "入庫")
                .param("reason", "通常入荷"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inventory"));
    }

    @Test
    void 出庫フォームを表示する() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/inventory/subtract"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventory/subtract"))
                .andExpect(model().attributeExists("inventoryHistory"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("type"));
    }

    @Test
    void 出庫処理を実行する() throws Exception {
        when(inventoryService.addInventoryHistory(any(InventoryHistory.class)))
                .thenReturn(testHistory);

        mockMvc.perform(post("/inventory/process")
                .with(csrf())
                .param("productId", "1")
                .param("quantityChange", "-10")
                .param("type", "出庫")
                .param("reason", "販売"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inventory"));
    }

    @Test
    void 在庫履歴を表示する() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryService.findByProductId(1L)).thenReturn(Arrays.asList(testHistory));

        mockMvc.perform(get("/inventory/history/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventory/history"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("inventoryHistories"));
    }

    @Test
    void 存在しない商品の在庫履歴を表示しようとすると404エラー() throws Exception {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/inventory/history/999"))
                .andExpect(status().isNotFound());
    }
}