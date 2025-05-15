package com.example.productmgr.e2e;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "E2E tests are disabled in CI environment")
public class AuthE2ETest extends BaseE2ETest {
    
    @Test
    public void testSuccessfulLogin() {
        // ログインページにアクセス
        driver.get(baseUrl + "/login");
        
        // ログインフォームが表示されることを確認
        WebElement form = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));
        assertNotNull(form);
        
        // ログイン情報を入力
        driver.findElement(By.name("username")).sendKeys(ADMIN_USERNAME);
        driver.findElement(By.name("password")).sendKeys(ADMIN_PASSWORD);
        
        // ログインボタンをクリック
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // ホームページにリダイレクトされることを確認
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));
        
        // ウェルカムメッセージが表示されることを確認
        WebElement welcome = driver.findElement(By.tagName("h5"));
        assertTrue(welcome.getText().contains("在庫管理システム"));
    }
    
    @Test
    public void testFailedLogin() {
        // ログインページにアクセス
        driver.get(baseUrl + "/login");
        
        // 無効なログイン情報を入力
        driver.findElement(By.name("username")).sendKeys("invalid");
        driver.findElement(By.name("password")).sendKeys("invalid");
        
        // ログインボタンをクリック
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        // エラーメッセージが表示されることを確認
        WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".alert-danger")));
        assertTrue(errorMessage.getText().contains("ユーザー名またはパスワードが正しくありません"));
    }
    
    @Test
    public void testLogout() {
        // ログイン
        login();
        
        // ホームページにアクセスしていることを確認
        assertEquals(baseUrl + "/", driver.getCurrentUrl());
        
        // ログアウトリンクをクリック
        WebElement logoutLink = driver.findElement(By.linkText("ログアウト"));
        logoutLink.click();
        
        // ログインページにリダイレクトされることを確認
        wait.until(ExpectedConditions.urlContains("/login"));
    }
    
    @Test
    public void testAccessProtectedPageWithoutAuth() {
        // 認証なしで保護されたページにアクセス
        driver.get(baseUrl + "/products");
        
        // ログインページにリダイレクトされることを確認
        wait.until(ExpectedConditions.urlContains("/login"));
    }
}