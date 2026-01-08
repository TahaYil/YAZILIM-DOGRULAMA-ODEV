package com.taa.tshirtsatis;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String FRONTEND_URL = "http://localhost:3001";
    protected static final String LOGIN_EMAIL = "admin@admin.com";
    protected static final String LOGIN_PASSWORD = "admin";

    @BeforeEach
    public void setUp() {
        ensureAdminUserExists();
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test başlamadan önce admin kullanıcısı yoksa ekler.
     */
    protected void ensureAdminUserExists() {
        try {
            URL url = new URL("http://localhost:8080/api/auth/signup");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{" +
                    "\"email\": \"admin@admin.com\"," +
                    "\"password\": \"admin\"," +
                    "\"gender\": \"MALE\"," +
                    "\"role\": \"ADMIN\"}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            // 200, 201, 409 (zaten varsa) kabul edilir
            if (code != 200 && code != 201 && code != 409) {
                System.err.println("Admin kullanıcı eklenemedi! HTTP code: " + code);
            }
        } catch (Exception e) {
            System.err.println("Admin kullanıcı eklenirken hata: " + e.getMessage());
        }
    }

    protected void login() {
        driver.get(FRONTEND_URL + "/auth/login");
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                org.openqa.selenium.By.name("email")));

        driver.findElement(org.openqa.selenium.By.name("email")).sendKeys(LOGIN_EMAIL);
        driver.findElement(org.openqa.selenium.By.name("password")).sendKeys(LOGIN_PASSWORD);
        driver.findElement(org.openqa.selenium.By.xpath("//button[@type='submit']")).click();

        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/products"));
    }
}
