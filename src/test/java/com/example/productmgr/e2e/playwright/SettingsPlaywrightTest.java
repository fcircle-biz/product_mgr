package com.example.productmgr.e2e.playwright;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SettingsPlaywrightTest extends BasePlaywrightTest {

    @Test
    public void testSettingsPageAccess() {
        // 管理者としてログイン
        login();
        
        // 設定ページに移動
        page.navigate(baseUrl + "/settings");
        page.waitForLoadState();
        
        // ページが正しく読み込まれていることを確認
        assertTrue(page.url().contains("/settings"), "設定ページに正しく移動していること");
        
        // 基本設定フォームが表示されていることを確認
        assertTrue(page.isVisible("form"), "設定フォームが表示されていること");
    }
    
    @Test
    public void testSettingsAccessControl() {
        // 既存のコンテキストを閉じて新しいコンテキストとページを作成（セッションがクリアされた状態で始める）
        context.close();
        context = browser.newContext();
        page = context.newPage();
        
        // ログインせずにアクセス
        page.navigate(baseUrl + "/settings");
        page.waitForLoadState();
        
        // リダイレクト先がログイン画面であることを確認
        assertTrue(page.url().contains("/login"), "認証なしのアクセスは認証ページへリダイレクトされること");
    }
}