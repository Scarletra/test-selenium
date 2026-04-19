package com.scarletra;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class GmailTest {
    WebDriver driver;
    WebDriverWait wait;

    // https://www.icertglobal.com/community/automate-gmail-login-with-selenium-webdriver-java
    // Use WebDriverWait to mimic human behavior and avoid detection by Google as a bot (scripted)


    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }

    @Test
    public void testGmailScenario() {
        try {
            driver.get("https://mail.google.com/mail/");

            WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("identifierId")));
            emailInput.sendKeys("anonym100205@gmail.com");
            driver.findElement(By.id("identifierNext")).click();

            WebElement passInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("Passwd")));
            passInput.sendKeys("Scarletra05");
            driver.findElement(By.id("passwordNext")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr.zE")));
            List<WebElement> unreadEmails = driver.findElements(By.cssSelector("tr.zE"));

            int count = Math.min(5, unreadEmails.size());
            
            System.out.println("----------------------------------------------");
            System.out.println("LOG: Showing " + count + " last unread emails:");
            
            for (int i = 0; i < count; i++) {
                WebElement emailRow = unreadEmails.get(i);
                String subject = emailRow.findElement(By.cssSelector("span.bog")).getText();
                System.out.println((i + 1) + ". Subject: " + subject);
            }
            
            System.out.println("----------------------------------------------");
            
            Assert.assertTrue(unreadEmails.size() >= 5, "Email unread is less than 5.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() {
        // driver.quit();
    }
}