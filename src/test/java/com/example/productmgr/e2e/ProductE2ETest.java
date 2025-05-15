package com.example.productmgr.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "E2E tests are disabled in CI environment")
public class ProductE2ETest extends BaseE2ETest {
    
    @BeforeEach
    public void loginBeforeEach() {
        login();
    }
    
    @Test
    public void testProductList() {
        // 商品一覧ページにアクセス
        driver.get(baseUrl + "/products");
        
        // ページタイトルを確認
        WebElement title = driver.findElement(By.tagName("h3"));
        assertEquals("商品一覧", title.getText());
        
        // 商品テーブルが表示されることを確認
        WebElement table = driver.findElement(By.tagName("table"));
        assertNotNull(table);
        
        // 商品登録ボタンが表示されることを確認
        WebElement createButton = driver.findElement(By.linkText("商品登録"));
        assertNotNull(createButton);
    }
    
    @Test
    public void testCreateProduct() {
        // 商品登録ページにアクセス
        driver.get(baseUrl + "/products/new");
        
        // フォームタイトルを確認
        WebElement title = driver.findElement(By.tagName("h3"));
        assertEquals("商品登録", title.getText());
        
        // 商品情報を入力
        driver.findElement(By.name("name")).sendKeys("テスト商品");
        driver.findElement(By.name("description")).sendKeys("これはテスト用の商品です");
        driver.findElement(By.name("price")).sendKeys("1500");
        driver.findElement(By.name("alertThreshold")).sendKeys("10");
        
        // カテゴリを選択
        Select categorySelect = new Select(driver.findElement(By.name("categoryId")));
        if (categorySelect.getOptions().size() > 1) {
            categorySelect.selectByIndex(1);
        }
        
        // 送信ボタンをクリック
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // 商品詳細ページにリダイレクトされることを確認
        wait.until(ExpectedConditions.urlMatches(".*/products/\\d+"));
        
        // 登録した商品名が表示されることを確認
        WebElement productName = driver.findElement(By.xpath("//td[text()='テスト商品']"));
        assertNotNull(productName);
    }
    
    @Test
    public void testEditProduct() {
        // 商品一覧ページにアクセス
        driver.get(baseUrl + "/products");
        
        // 商品が存在する場合、最初の商品の編集ボタンをクリック
        List<WebElement> editButtons = driver.findElements(By.linkText("編集"));
        if (!editButtons.isEmpty()) {
            editButtons.get(0).click();
            
            // 編集フォームが表示されることを確認
            WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h3")));
            assertEquals("商品編集", title.getText());
            
            // 商品名を変更
            WebElement nameInput = driver.findElement(By.name("name"));
            nameInput.clear();
            nameInput.sendKeys("更新された商品名");
            
            // 更新ボタンをクリック
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            
            // 商品詳細ページにリダイレクトされることを確認
            wait.until(ExpectedConditions.urlMatches(".*/products/\\d+"));
            
            // 更新した商品名が表示されることを確認
            WebElement updatedName = driver.findElement(By.xpath("//td[contains(text(),'更新された商品名')]"));
            assertNotNull(updatedName);
        }
    }
    
    @Test
    public void testSearchProduct() {
        // 商品一覧ページにアクセス
        driver.get(baseUrl + "/products");
        
        // 検索フォームに入力
        driver.findElement(By.name("keyword")).sendKeys("テスト");
        
        // 検索ボタンをクリック
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // URLに検索パラメータが含まれることを確認
        wait.until(ExpectedConditions.urlContains("keyword=テスト"));
        
        // 検索結果の確認（結果がある場合）
        List<WebElement> rows = driver.findElements(By.cssSelector("tbody tr"));
        for (WebElement row : rows) {
            String rowText = row.getText().toLowerCase();
            // 検索キーワードが含まれているか、結果が空でないことを確認
            assertTrue(rowText.contains("テスト") || rows.isEmpty());
        }
    }
    
    @Test
    public void testProductDetail() {
        // 商品一覧ページにアクセス
        driver.get(baseUrl + "/products");
        
        // 商品が存在する場合、最初の商品の詳細リンクをクリック
        List<WebElement> detailLinks = driver.findElements(By.linkText("詳細"));
        if (!detailLinks.isEmpty()) {
            detailLinks.get(0).click();
            
            // 商品詳細ページが表示されることを確認
            WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h3")));
            assertEquals("商品詳細", title.getText());
            
            // 在庫履歴ボタンが表示されることを確認
            WebElement historyButton = driver.findElement(By.linkText("在庫履歴"));
            assertNotNull(historyButton);
            
            // 編集ボタンが表示されることを確認
            WebElement editButton = driver.findElement(By.linkText("編集"));
            assertNotNull(editButton);
        }
    }
}