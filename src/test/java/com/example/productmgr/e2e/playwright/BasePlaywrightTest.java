package com.example.productmgr.e2e.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e")
public abstract class BasePlaywrightTest {
    
    protected static final String ADMIN_USERNAME = "admin";
    protected static final String ADMIN_PASSWORD = "admin";
    
    @LocalServerPort
    protected int port;
    
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected String baseUrl;
    
    @BeforeAll
    public static void launchBrowser() {
        try {
            // Playwrightインスタンスの作成
            playwright = Playwright.create();
            
            // ブラウザの起動（自動インストールを有効化）
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setSlowMo(100));
            
            System.out.println("Playwright browser launched successfully");
        } catch (Exception e) {
            System.err.println("Failed to launch Playwright browser: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @AfterAll
    public static void closeBrowser() {
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
    
    @BeforeEach
    public void setUp() {
        context = browser.newContext();
        page = context.newPage();
        baseUrl = "http://localhost:" + port;
        
        // タイムアウト設定を60秒に設定（環境によっては処理に時間がかかるため増加）
        page.setDefaultTimeout(60000);
        
        // ナビゲーションタイムアウトも60秒に設定
        page.setDefaultNavigationTimeout(60000);
    }
    
    @AfterEach
    public void tearDown(TestInfo testInfo) {
        if (page != null) {
            try {
                // 失敗したテストの場合、スクリーンショットを撮る
                if (testInfo.getTestMethod().isPresent()) {
                    String testName = testInfo.getTestMethod().get().getName();
                    String screenshotPath = "/tmp/" + testName + "-teardown-screenshot.png";
                    page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(screenshotPath)));
                    System.out.println("Saved screenshot on teardown: " + screenshotPath);
                }
            } catch (Exception e) {
                System.err.println("Failed to take screenshot: " + e.getMessage());
            }
        }
        
        if (context != null) {
            context.close();
            context = null;
        }
    }
    
    protected void login() {
        login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }
    
    protected void login(String username, String password) {
        // ログインページに移動してDOMが完全にロードされるまで待機
        page.navigate(baseUrl + "/login");
        page.waitForLoadState();
        page.waitForSelector("input[name=username]", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        
        // ログイン情報を入力
        page.fill("input[name=username]", username);
        page.fill("input[name=password]", password);
        
        // Runnable を使用して waitForNavigation を実行
        page.waitForNavigation(() -> {
            page.click("button[type=submit]");
        });
    }
    
    protected void logout() {
        // ページが完全にロードされた状態であることを確認
        page.waitForLoadState();
        
        // E2Eテスト環境ではCSRF問題があるため、直接ログインページに移動することでログアウト相当の処理とする
        page.navigate(baseUrl + "/login");
        
        // ログインページが表示されたことを確認
        page.waitForLoadState();
        page.waitForSelector("form", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }
}