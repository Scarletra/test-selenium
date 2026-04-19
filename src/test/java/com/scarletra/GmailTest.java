package com.scarletra;

import io.github.bonigarcia.wdm.WebDriverManager;
import com.warrenstrange.googleauth.GoogleAuthenticator;
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
import java.util.Collections;
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
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
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

            String secretKey = "xq4ntt7ifysxfwiajgy65hjzbkmzz2pg"; 
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            String otpCode = String.valueOf(gAuth.getTotpPassword(secretKey));

            WebElement otpInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("totpPin")));
            otpInput.sendKeys(otpCode);
            driver.findElement(By.id("totpNext")).click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("tr.zE")));
            List<WebElement> unreadEmails = driver.findElements(By.cssSelector("tr.zE"));
            
            Assert.assertTrue(unreadEmails.size() > 0, "No unread emails found.");

            WebElement lastUnread = unreadEmails.get(0);
            String subject = lastUnread.findElement(By.cssSelector("span.bog")).getText();
            
            System.out.println("----------------------------------------------");
            System.out.println("LOG: Last Unread Email Title: " + subject);
            System.out.println("----------------------------------------------");

            WebElement checkbox = lastUnread.findElement(By.cssSelector("div[role='checkbox']"));
            checkbox.click();

            WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[aria-label='Delete']")));
            deleteButton.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'moved to Bin')]")));
            System.out.println("LOG: Successfully deleted email: " + subject);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test failed due to: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {
        // driver.quit();
    }
}