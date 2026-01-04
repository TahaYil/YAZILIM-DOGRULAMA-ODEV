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

public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String FRONTEND_URL = "http://localhost:3001";
    protected static final String LOGIN_EMAIL = "a@gmail.com";
    protected static final String LOGIN_PASSWORD = "123";

    @BeforeEach
    public void setUp() {
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

    protected void login() {
        driver.get(FRONTEND_URL + "/auth/login");
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
            org.openqa.selenium.By.name("email")
        ));
        
        driver.findElement(org.openqa.selenium.By.name("email")).sendKeys(LOGIN_EMAIL);
        driver.findElement(org.openqa.selenium.By.name("password")).sendKeys(LOGIN_PASSWORD);
        driver.findElement(org.openqa.selenium.By.xpath("//button[@type='submit']")).click();
        
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/products"));
    }
}

