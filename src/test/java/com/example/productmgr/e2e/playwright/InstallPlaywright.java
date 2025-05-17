package com.example.productmgr.e2e.playwright;

import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

/**
 * このクラスはPlaywrightのブラウザをインストールするためのシンプルなテストクラスです。
 * E2Eテスト実行前に自動的にブラウザをインストールします。
 */
public class InstallPlaywright {
    
    @Test
    public void installPlaywrightBrowsers() {
        // このメソッドを実行するとPlaywrightは自動的に必要なブラウザをインストールします
        try (Playwright playwright = Playwright.create()) {
            System.out.println("Playwright created successfully");
            System.out.println("Browser binaries will be installed on first browser launch");
        } catch (Exception e) {
            System.err.println("Failed to create Playwright instance: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}