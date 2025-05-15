package com.example.productmgr.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("e2e")
@Testcontainers
public abstract class BaseE2ETest {
    
    protected static final String ADMIN_USERNAME = "admin";
    protected static final String ADMIN_PASSWORD = "admin123";
    
    @LocalServerPort
    protected int port;
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("e2e-schema.sql");
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @BeforeAll
    public static void setupDriver() {
        String browserType = System.getProperty("selenium.browser", "chrome");
        if ("firefox".equalsIgnoreCase(browserType)) {
            WebDriverManager.firefoxdriver().setup();
        } else {
            WebDriverManager.chromedriver().setup();
        }
    }
    
    @BeforeEach
    public void setUp() {
        String browserType = System.getProperty("selenium.browser", "chrome");
        
        if ("firefox".equalsIgnoreCase(browserType)) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
            driver = new FirefoxDriver(options);
        } else {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--remote-allow-origins=*");
            
            // Chrome binary path for different environments
            String chromePath = System.getenv("CHROME_BIN");
            if (chromePath != null) {
                options.setBinary(chromePath);
            }
            
            try {
                driver = new ChromeDriver(options);
            } catch (Exception e) {
                System.err.println("Failed to start Chrome driver. Trying with different config...");
                options = new ChromeOptions();
                options.addArguments("--headless");
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                driver = new ChromeDriver(options);
            }
        }
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }
    
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    protected void login() {
        login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }
    
    protected void login(String username, String password) {
        driver.get(baseUrl + "/login");
        driver.findElement(org.openqa.selenium.By.name("username")).sendKeys(username);
        driver.findElement(org.openqa.selenium.By.name("password")).sendKeys(password);
        driver.findElement(org.openqa.selenium.By.cssSelector("button[type='submit']")).click();
    }
    
    protected void logout() {
        driver.findElement(org.openqa.selenium.By.linkText("ログアウト")).click();
    }
}