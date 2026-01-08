package com.taa.tshirtsatis;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;

import java.time.Duration;
import java.util.List;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.taa.tshirtsatis.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String FRONTEND_URL = "http://localhost:3001";
    protected static final String LOGIN_EMAIL = "admin@admin.com";
    protected static final String LOGIN_PASSWORD = "admin";
    @Autowired
    protected UsersRepository usersRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;

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

            if (usersRepository.findByEmail("admin@admin.com").isEmpty()) {
                Users adminUser = Users.builder()
                        .email("admin@admin.com")
                        .password(passwordEncoder.encode("admin"))
                        .role(Role.ADMIN)
                        .gender(Gender.MALE)
                        .build();
                usersRepository.save(adminUser);
                System.out.println("[DataInitializer] Admin user created: admin@admin.com / admin");
            } else {
                System.out.println("[DataInitializer] Admin user already exists: admin@admin.com");
            }
        } catch (Exception e) {
            System.err.println("[DataInitializer] Admin user could not be created! Exception: " + e.getMessage());
            e.printStackTrace();
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
