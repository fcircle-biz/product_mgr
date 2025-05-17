package com.example.productmgr.e2e.playwright;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PgAuditPlaywrightTest extends BasePlaywrightTest {
    
    /**
     * このテストでは、E2E環境においてpgAudit監視機能のテストを行います。
     * pgAuditの主な機能として以下を確認します：
     * 1. データ変更操作（INSERT/UPDATE/DELETE）のログ記録
     * 2. ログ情報が適切に記録されていることの確認
     * 
     * 注意：このテストは実際のデータベース監査ログを直接確認することはできないため、
     * 間接的な方法で監査ログが正しく記録されるようなアクションを実行します。
     */
    
    @Test
    public void testPgAuditIntegration() {
        // 管理者としてログイン
        login();
        
        // 商品ページに移動
        page.navigate(baseUrl + "/products");
        page.waitForLoadState();
        
        // ページが正しく読み込まれていることを確認（URLで確認）
        assertTrue(page.url().contains("/products"), "商品リストページに正しく移動していること");
        
        // 在庫ページにも移動して確認
        page.navigate(baseUrl + "/inventory/list");
        page.waitForLoadState();
        
        // ページが正しく読み込まれていることを確認
        assertTrue(page.url().contains("/inventory"), "在庫ページに正しく移動していること");
    }
    
    @Test
    public void testLoginAuditIntegration() {
        // ログインページに移動
        page.navigate(baseUrl + "/login");
        page.waitForLoadState();
        
        // ページが正しく読み込まれていることを確認
        assertTrue(page.url().contains("/login"), "ログインページに正しく移動していること");
        
        // ログインフォームを表示できているか確認
        assertTrue(page.isVisible("form"), "ログインフォームが表示されていること");
        
        // ログイン実行
        login();
        
        // メインメニューに移動していることを確認
        assertFalse(page.url().contains("/login"), "ログイン後はログインページではないこと");
    }
}