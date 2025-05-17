package com.example.productmgr.e2e.playwright;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportPlaywrightTest extends BasePlaywrightTest {

    @Test
    public void testReportAccess() {
        // ログイン
        login();
        
        // レポートダッシュボードに移動
        page.navigate(baseUrl + "/reports");
        page.waitForLoadState();
        
        // URLが正しいことを確認
        assertTrue(page.url().contains("/reports"), "レポートページに正しく移動していること");
        
        // ログアウト
        logout();
        
        // コンテキストを閉じて新しいコンテキストとページを作成（完全にセッションをクリア）
        context.close();
        context = browser.newContext();
        page = context.newPage();
        
        // 認証なしでアクセスすると、ログインページにリダイレクトされることを確認
        page.navigate(baseUrl + "/reports");
        page.waitForLoadState();
        
        // リダイレクト先がログイン画面であることを確認
        assertTrue(page.url().contains("/login"), "認証なしのアクセスは認証ページへリダイレクトされること");
    }
    
    @Test
    public void testAllReportPagesAccess() {
        // ログイン
        login();
        
        // 各レポートページに移動し、アクセスできることを確認
        String[] reportPaths = {
            "/reports/stock-warning",
            "/reports/inventory-summary",
            "/reports/category-distribution",
            "/reports/inventory-turnover",
            "/reports/daily",
            "/reports/monthly"
        };
        
        for (String path : reportPaths) {
            // ページに移動
            page.navigate(baseUrl + path);
            page.waitForLoadState();
            
            // URLが正しいことを確認
            assertTrue(page.url().contains(path), path + "ページに正しく移動していること");
            
            // HTTPステータスコードが200であることを確認（リダイレクトされていないこと）
            assertFalse(page.url().contains("/login"), "ログインページにリダイレクトされていないこと");
        }
    }
}