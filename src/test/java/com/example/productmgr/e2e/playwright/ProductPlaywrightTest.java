package com.example.productmgr.e2e.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "E2E tests are disabled in CI environment")
public class ProductPlaywrightTest extends BasePlaywrightTest {
    
    @BeforeEach
    public void loginBeforeEach() {
        login();
    }
    
    @Test
    public void testProductList() {
        // 商品一覧ページにアクセス
        page.navigate(baseUrl + "/products");
        page.waitForLoadState();
        
        // ページタイトルが表示されるまで待機
        page.waitForSelector("h2", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // ページタイトルを確認
        Locator title = page.locator("h2");
        assertEquals("商品一覧", title.textContent());
        
        // 商品テーブルが表示されることを確認
        assertTrue(page.isVisible("table"));
        
        // 商品登録ボタンが表示されることを確認
        assertTrue(page.isVisible("a:has-text('新規登録')"));
    }
    
    @Test
    public void testCreateProduct() {
        // 商品登録ページにアクセス
        page.navigate(baseUrl + "/products/new");
        page.waitForLoadState();
        
        // フォームタイトルが表示されるまで待機
        page.waitForSelector("h2", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // フォームタイトルを確認
        Locator title = page.locator("h2");
        assertEquals("商品登録", title.textContent());
        
        // 商品情報を入力
        page.fill("input[name=name]", "Playwrightテスト商品");
        page.fill("textarea[name=description]", "これはPlaywrightテスト用の商品です");
        page.fill("input[name=price]", "2500");
        page.fill("input[name=alertThreshold]", "15");
        
        // カテゴリを選択（最初のオプション以外を選択）
        Locator categorySelect = page.locator("select[name=categoryId]");
        int optionsCount = categorySelect.locator("option").count();
        if (optionsCount > 1) {
            // 2番目のオプションを選択（インデックスは0から始まるため、1を指定）
            categorySelect.selectOption(new com.microsoft.playwright.options.SelectOption().setIndex(1));
        }
        
        // 送信ボタンをクリック
        page.click("button[type=submit]");
        
        // 商品詳細ページにリダイレクトされることを確認
        page.waitForURL("**/products/*");
        
        // 登録した商品名が表示されることを確認
        assertTrue(page.isVisible("text=Playwrightテスト商品"));
    }
    
    @Test
    public void testEditProduct() {
        // 商品一覧ページにアクセス
        page.navigate(baseUrl + "/products");
        
        // 編集リンクが存在する場合、最初の編集リンクをクリック（アイコンボタン）
        if (page.locator("a[title='編集']").count() > 0) {
            page.click("a[title='編集']:first-child");
            
            // 編集フォームが表示されることを確認
            page.waitForSelector("h2:has-text('商品編集')");
            
            // 商品名を変更
            page.fill("input[name=name]", "Playwright更新商品");
            
            // 更新ボタンをクリック
            page.click("button[type=submit]");
            
            // 商品詳細ページにリダイレクトされることを確認
            page.waitForURL("**/products/*");
            
            // 更新した商品名が表示されることを確認
            assertTrue(page.isVisible("text=Playwright更新商品"));
        }
    }
    
    @Test
    public void testSearchProduct() {
        // 商品一覧ページにアクセス
        page.navigate(baseUrl + "/products");
        page.waitForLoadState();
        
        // 検索フォームが表示されるまで待機
        page.waitForSelector("form input[name=keyword]", 
                              new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // 検索フォームに入力
        page.fill("form input[name=keyword]", "テスト");
        
        // 検索ボタンをクリック
        page.click("button[type=submit]");
        
        // URLに検索パラメータが含まれることを確認
        page.waitForURL("**/products?keyword=*");
        
        // 商品テーブルが表示されることを確認
        assertTrue(page.isVisible("table"));
    }
    
    @Test
    public void testProductDetail() {
        // 商品一覧ページにアクセス
        page.navigate(baseUrl + "/products");
        
        // 詳細リンクが存在する場合、最初の詳細リンクをクリック（アイコンボタン）
        if (page.locator("a[title='詳細']").count() > 0) {
            page.click("a[title='詳細']:first-child");
            
            // 商品詳細ページが表示されることを確認
            page.waitForSelector("h2:has-text('商品詳細')");
            
            // 在庫履歴ボタンが表示されることを確認
            assertTrue(page.isVisible("a:has-text('在庫履歴')"));
            
            // 編集ボタンが表示されることを確認
            assertTrue(page.isVisible("a:has-text('編集')"));
        }
    }
}