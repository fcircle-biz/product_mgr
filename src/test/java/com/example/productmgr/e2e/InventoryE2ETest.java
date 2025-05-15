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
public class InventoryE2ETest extends BaseE2ETest {
    
    @BeforeEach
    public void loginBeforeEach() {
        login();
    }
    
    @Test
    public void testInventoryList() {
        // 在庫一覧ページにアクセス
        driver.get(baseUrl + "/inventory");
        
        // ページタイトルを確認
        WebElement title = driver.findElement(By.tagName("h3"));
        assertEquals("在庫一覧", title.getText());
        
        // 在庫テーブルが表示されることを確認
        WebElement table = driver.findElement(By.tagName("table"));
        assertNotNull(table);
        
        // 入庫・出庫ボタンが表示されることを確認
        WebElement addButton = driver.findElement(By.linkText("入庫登録"));
        WebElement subtractButton = driver.findElement(By.linkText("出庫登録"));
        assertNotNull(addButton);
        assertNotNull(subtractButton);
    }
    
    @Test
    public void testAddInventory() {
        // 入庫登録ページにアクセス
        driver.get(baseUrl + "/inventory/add");
        
        // ページタイトルを確認
        WebElement title = driver.findElement(By.tagName("h3"));
        assertEquals("入庫登録", title.getText());
        
        // 商品選択
        Select productSelect = new Select(driver.findElement(By.name("productId")));
        if (productSelect.getOptions().size() > 1) {
            productSelect.selectByIndex(1);
        }
        
        // 入庫情報を入力
        driver.findElement(By.name("quantity")).sendKeys("10");
        Select reasonSelect = new Select(driver.findElement(By.name("reason")));
        reasonSelect.selectByValue("仕入れ");
        driver.findElement(By.name("note")).sendKeys("テスト入庫");
        
        // 登録ボタンをクリック
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // 在庫履歴ページにリダイレクトされることを確認
        wait.until(ExpectedConditions.urlMatches(".*/inventory/history/\\d+"));
        
        // 成功メッセージが表示されることを確認
        WebElement successMessage = driver.findElement(By.cssSelector(".alert-success"));
        assertTrue(successMessage.getText().contains("入庫登録が完了しました"));
    }
    
    @Test
    public void testSubtractInventory() {
        // 出庫登録ページにアクセス
        driver.get(baseUrl + "/inventory/subtract");
        
        // ページタイトルを確認
        WebElement title = driver.findElement(By.tagName("h3"));
        assertEquals("出庫登録", title.getText());
        
        // 商品選択
        Select productSelect = new Select(driver.findElement(By.name("productId")));
        if (productSelect.getOptions().size() > 1) {
            productSelect.selectByIndex(1);
        }
        
        // 出庫情報を入力
        driver.findElement(By.name("quantity")).sendKeys("5");
        Select reasonSelect = new Select(driver.findElement(By.name("reason")));
        reasonSelect.selectByValue("販売");
        driver.findElement(By.name("note")).sendKeys("テスト出庫");
        
        // 登録ボタンをクリック
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // 成功または在庫不足エラーを確認
        WebElement alert = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".alert")));
        String alertText = alert.getText();
        assertTrue(alertText.contains("出庫登録が完了しました") || 
                   alertText.contains("在庫が不足しています"));
    }
    
    @Test
    public void testInventoryHistory() {
        // 在庫一覧ページにアクセス
        driver.get(baseUrl + "/inventory");
        
        // 商品が存在する場合、最初の商品の履歴ボタンをクリック
        List<WebElement> historyButtons = driver.findElements(By.linkText("履歴"));
        if (!historyButtons.isEmpty()) {
            historyButtons.get(0).click();
            
            // 在庫履歴ページが表示されることを確認
            WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h3")));
            assertEquals("在庫履歴", title.getText());
            
            // 履歴テーブルが表示されることを確認
            WebElement table = driver.findElement(By.tagName("table"));
            assertNotNull(table);
        }
    }
    
    @Test
    public void testInventoryValidation() {
        // 出庫登録ページにアクセス
        driver.get(baseUrl + "/inventory/subtract");
        
        // 商品選択
        Select productSelect = new Select(driver.findElement(By.name("productId")));
        if (productSelect.getOptions().size() > 1) {
            productSelect.selectByIndex(1);
        }
        
        // 無効な数量を入力（負の値）
        driver.findElement(By.name("quantity")).sendKeys("-5");
        
        // 登録ボタンをクリック
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // バリデーションエラーが表示されることを確認
        WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".invalid-feedback, .alert-danger")));
        assertNotNull(errorMessage);
    }
}