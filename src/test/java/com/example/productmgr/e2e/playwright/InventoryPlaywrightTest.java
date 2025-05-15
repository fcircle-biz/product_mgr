package com.example.productmgr.e2e.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "E2E tests are disabled in CI environment")
public class InventoryPlaywrightTest extends BasePlaywrightTest {
    
    @BeforeEach
    public void loginBeforeEach() {
        login();
    }
    
    @Test
    public void testInventoryList() {
        // 在庫一覧ページにアクセス
        page.navigate(baseUrl + "/inventory");
        page.waitForLoadState();
        
        // ページタイトルが表示されるまで待機
        page.waitForSelector("h2", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // ページタイトルを確認
        Locator title = page.locator("h2");
        assertEquals("在庫管理", title.textContent());
        
        // 在庫テーブルが表示されることを確認
        assertTrue(page.isVisible("table"));
    }
    
    @Test
    public void testAddInventory() {
        // 商品一覧ページにアクセス
        page.navigate(baseUrl + "/products");
        
        // 商品がある場合、最初の商品の詳細リンクをクリック（アイコンボタン）
        if (page.locator("a[title='詳細']").count() > 0) {
            page.click("a[title='詳細']:first-child");
            
            // 商品詳細ページで在庫追加ボタンをクリック
            page.click("a:has-text('在庫追加')");
            
            // 在庫追加フォームが表示されることを確認
            page.waitForSelector("h2:has-text('在庫追加')");
            
            // 在庫追加情報を入力
            page.fill("input[name=quantityChange]", "10");
            page.fill("textarea[name=reason]", "Playwrightテスト用在庫追加");
            
            // 追加ボタンをクリック
            page.click("button[type=submit]");
            
            // 商品詳細ページにリダイレクトされることを確認
            page.waitForURL("**/products/*");
            
            // 成功メッセージが表示されることを確認
            assertTrue(page.isVisible(".alert-success"));
        }
    }
    
    @Test
    public void testSubtractInventory() {
        // 商品一覧ページにアクセス
        page.navigate(baseUrl + "/products");
        
        // 商品がある場合、最初の商品の詳細リンクをクリック（アイコンボタン）
        if (page.locator("a[title='詳細']").count() > 0) {
            page.click("a[title='詳細']:first-child");
            
            // 商品詳細ページで在庫削減ボタンをクリック
            page.click("a:has-text('在庫削減')");
            
            // 在庫削減フォームが表示されることを確認
            page.waitForSelector("h2:has-text('在庫削減')");
            
            // 在庫削減情報を入力
            page.fill("input[name=quantityChange]", "5");
            page.fill("textarea[name=reason]", "Playwrightテスト用在庫削減");
            
            // 削減ボタンをクリック
            page.click("button[type=submit]");
            
            // 商品詳細ページにリダイレクトされることを確認
            page.waitForURL("**/products/*");
            
            // 成功メッセージが表示されることを確認
            assertTrue(page.isVisible(".alert-success"));
        }
    }
    
    @Test
    public void testInventoryHistory() {
        // 商品一覧ページにアクセス
        page.navigate(baseUrl + "/products");
        
        // 商品がある場合、最初の商品の詳細リンクをクリック（アイコンボタン）
        if (page.locator("a[title='詳細']").count() > 0) {
            page.click("a[title='詳細']:first-child");
            
            // 商品詳細ページで在庫履歴ボタンをクリック
            page.click("a:has-text('在庫履歴')");
            
            // 在庫履歴ページが表示されることを確認
            page.waitForSelector("h2:has-text('在庫履歴')");
            
            // 在庫履歴テーブルが表示されることを確認
            assertTrue(page.isVisible("table"));
        }
    }
}