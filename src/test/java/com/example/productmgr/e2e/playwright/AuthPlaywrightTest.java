package com.example.productmgr.e2e.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "E2E tests are disabled in CI environment")
public class AuthPlaywrightTest extends BasePlaywrightTest {
    
    @Test
    public void testSuccessfulLogin() {
        // ログインページへアクセス
        page.navigate(baseUrl + "/login");
        page.waitForLoadState();
        
        // ページタイトルを確認
        assertEquals("ログイン | 商品管理システム", page.title());
        
        // ログインフォームが表示されるまで待機
        page.waitForSelector("form", 
                           new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // ログインフォームが表示されることを確認
        assertTrue(page.isVisible("form"));
        
        // 入力フィールドが表示されるまで待機
        page.waitForSelector("input[name=username]", 
                            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForSelector("input[name=password]", 
                            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // 認証情報を入力
        page.fill("input[name=username]", ADMIN_USERNAME);
        page.fill("input[name=password]", ADMIN_PASSWORD);
        
        // ログインボタンをクリックしてナビゲーションを待機
        page.waitForNavigation(() -> {
            page.click("button[type=submit]");
        });
        
        // メインメニューページにリダイレクトされるのを待機（タイムアウトを長めに設定）
        try {
            page.waitForURL("**/menu", new Page.WaitForURLOptions().setTimeout(60000));
            page.waitForLoadState();
            
            // メインメニューのタイトルが表示されるまで待機（タイムアウトを長めに設定）
            page.waitForSelector(".welcome-banner", 
                                new Page.WaitForSelectorOptions()
                                    .setState(WaitForSelectorState.VISIBLE)
                                    .setTimeout(60000));
        } catch (Exception e) {
            System.err.println("Navigation or element wait timed out: " + e.getMessage());
            System.err.println("Current URL: " + page.url());
            System.err.println("Page content: " + page.content());
            throw e;
        }
        
        // メインメニューが表示されることを確認
        assertTrue(page.isVisible(".welcome-banner"));
    }
    
    @Test
    public void testFailedLogin() {
        // ログインページへアクセス
        page.navigate(baseUrl + "/login");
        page.waitForLoadState();
        
        // 入力フィールドが表示されるまで待機
        page.waitForSelector("input[name=username]", 
                            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForSelector("input[name=password]", 
                            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // 不正な認証情報を入力
        page.fill("input[name=username]", ADMIN_USERNAME);
        page.fill("input[name=password]", "wrong-password");
        
        // ログインボタンをクリック
        page.click("button[type=submit]");
        
        // エラーメッセージが表示されるまで待機
        page.waitForSelector(".alert-danger", 
                            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // エラーメッセージが表示されることを確認
        assertTrue(page.isVisible(".alert-danger"));
    }
    
    @Test
    public void testLogout() {
        // ログイン
        login();
        
        // メインメニューが表示されることを確認（タイムアウトを長めに設定）
        try {
            page.waitForURL("**/menu", new Page.WaitForURLOptions().setTimeout(60000));
            page.waitForLoadState();
            
            page.waitForSelector(".welcome-banner", 
                              new Page.WaitForSelectorOptions()
                                  .setState(WaitForSelectorState.VISIBLE)
                                  .setTimeout(60000));
            
            // 要素が見つかったことを確認
            assertTrue(page.isVisible(".welcome-banner"));
            System.out.println("Found welcome banner");
        } catch (Exception e) {
            System.err.println("Element wait timed out in testLogout: " + e.getMessage());
            System.err.println("Current URL: " + page.url());
            System.err.println("Page content: " + page.content());
            throw e;
        }
        
        // ナビゲーションバーが表示されていることを確認
        page.waitForSelector("nav.navbar", 
                             new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // ログアウトリンクが表示されていることを確認
        page.waitForSelector("a:has-text('ログアウト')", 
                             new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // ログアウト処理を実行
        page.waitForNavigation(() -> {
            page.click("a:has-text('ログアウト')");
        });
        
        // ログインページへリダイレクトされることを確認
        page.waitForURL("**/login**");
        page.waitForLoadState();
        
        // ログインフォームが表示されるまで待機
        page.waitForSelector("form", 
                           new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // ログインフォームが表示されることを確認
        assertTrue(page.isVisible("form"));
        
        // ログアウト成功メッセージが表示されるまで待機
        page.waitForSelector(".alert-success", 
                            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // ログアウト成功メッセージが表示されることを確認
        assertTrue(page.isVisible(".alert-success"));
    }
}