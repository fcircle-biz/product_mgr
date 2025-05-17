package com.example.productmgr.service;

import com.example.productmgr.model.Category;
import com.example.productmgr.model.Product;
import com.example.productmgr.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // カテゴリーの準備
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("電化製品");

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
    }

    @Test
    void 全商品を取得する() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllWithCategory()).thenReturn(products);

        List<Product> result = productService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("テスト商品");
        verify(productRepository).findAllWithCategory();
    }

    @Test
    void IDで商品を取得する() {
        when(productRepository.findByIdWithCategory(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("テスト商品");
        verify(productRepository).findByIdWithCategory(1L);
    }

    @Test
    void キーワードで商品を検索する() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.searchByKeyword("テスト")).thenReturn(products);

        List<Product> result = productService.searchByKeyword("テスト");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("テスト商品");
        verify(productRepository).searchByKeyword("テスト");
    }

    @Test
    void カテゴリIDで商品を検索する() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryId(1L)).thenReturn(products);

        List<Product> result = productService.findByCategoryId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
        verify(productRepository).findByCategoryId(1L);
    }

    @Test
    void ステータスで商品を検索する() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByStatus("販売中")).thenReturn(products);

        List<Product> result = productService.findByStatus("販売中");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("販売中");
        verify(productRepository).findByStatus("販売中");
    }

    @Test
    void 在庫切れ商品を取得する() {
        testProduct.setStockQuantity(0);
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findOutOfStock()).thenReturn(products);

        List<Product> result = productService.findOutOfStock();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQuantity()).isEqualTo(0);
        verify(productRepository).findOutOfStock();
    }

    @Test
    void 在庫少商品を取得する() {
        testProduct.setStockQuantity(5);
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findLowStock()).thenReturn(products);

        List<Product> result = productService.findLowStock();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQuantity()).isEqualTo(5);
        verify(productRepository).findLowStock();
    }

    @Test
    void 新規商品を保存する() {
        Product newProduct = new Product();
        newProduct.setName("新規商品");
        newProduct.setCategoryId(1L);
        newProduct.setJanCode("9876543210123");
        newProduct.setPrice(new BigDecimal("1500"));
        newProduct.setStockUnit("個");

        when(categoryService.existsById(1L)).thenReturn(true);
        when(productRepository.existsByJanCode("9876543210123")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.save(newProduct);

        assertThat(result).isEqualTo(testProduct);
        verify(categoryService).existsById(1L);
        verify(productRepository).existsByJanCode("9876543210123");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void 存在しないカテゴリで商品を保存するとエラー() {
        Product newProduct = new Product();
        newProduct.setCategoryId(999L);

        when(categoryService.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> productService.save(newProduct))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("指定されたカテゴリが存在しません");

        verify(categoryService).existsById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void 重複したJANコードで商品を保存するとエラー() {
        Product newProduct = new Product();
        newProduct.setCategoryId(1L);
        newProduct.setJanCode("1234567890123");

        when(categoryService.existsById(1L)).thenReturn(true);
        when(productRepository.existsByJanCode("1234567890123")).thenReturn(true);

        assertThatThrownBy(() -> productService.save(newProduct))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("このJANコードは既に登録されています");

        verify(categoryService).existsById(1L);
        verify(productRepository).existsByJanCode("1234567890123");
        verify(productRepository, never()).save(any());
    }

    @Test
    void 既存商品を更新する() {
        Product updateProduct = new Product();
        updateProduct.setId(1L);
        updateProduct.setName("更新商品");
        updateProduct.setCategoryId(1L);
        updateProduct.setJanCode("1234567890123");
        updateProduct.setPrice(new BigDecimal("2000"));
        updateProduct.setStockUnit("個");

        when(categoryService.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updateProduct);

        Product result = productService.save(updateProduct);

        assertThat(result.getName()).isEqualTo("更新商品");
        verify(categoryService).existsById(1L);
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void 商品を削除する() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteById(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void 商品が存在するか確認する() {
        when(productRepository.existsById(1L)).thenReturn(true);

        boolean result = productService.existsById(1L);

        assertThat(result).isTrue();
        verify(productRepository).existsById(1L);
    }
}