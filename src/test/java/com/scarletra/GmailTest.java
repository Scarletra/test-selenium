package com.scarletra;

import io.github.bonigarcia.wdm.WebDriverManager;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        options.addArguments("--lang=en-US"); 
        
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

            try { Thread.sleep(3000); } catch (InterruptedException e) {}

            // Reference:
            // https://github.com/shaffan15/selenium-2fa-login-automation/blob/main/src/test/java/auth/WorkplaceLoginTest.java
            // https://www.giaphi.com/2025/01/automate-google-mfa-login-with-selenium.html
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                
                try {
                    shortWait.until(ExpectedConditions.presenceOfElementLocated(By.id("totpPin")));
                } catch (Exception e1) {
                    try {
                        WebElement authOption = shortWait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//div[@data-challengetype='6']")
                        ));
                        authOption.click();
                    } catch (Exception e2) {
                        WebElement tryAnotherWay = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//span[text()='Try another way']/ancestor::button")
                        ));
                        tryAnotherWay.click();
                        
                        Thread.sleep(2000);
                        
                        WebElement authOptionAfterClick = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//div[@data-challengetype='6']")
                        ));
                        authOptionAfterClick.click();
                    }
                }
            } catch (Exception eFinal) {
                System.out.println("LOG: 2FA Flow failed or not needed: " + eFinal.getMessage());
            }

            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            String secretKey = "xq4ntt7ifysxfwiajgy65hjzbkmzz2pg"; 
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            int rawOtp = gAuth.getTotpPassword(secretKey);
            String otpCode = String.format("%06d", rawOtp);

            WebElement otpInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("totpPin")));
            otpInput.sendKeys(otpCode);
            driver.findElement(By.id("totpNext")).click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.F.cf.zt")));

            List<WebElement> unreadEmails = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("tr.zE")));
            Assert.assertTrue(unreadEmails.size() > 0, "No unread emails found.");
            String unreadSubject = unreadEmails.get(0).findElement(By.cssSelector("span.bog")).getText();

            System.out.println("----------------------------------------------");
            System.out.println("LOG: Last Unread Email Title: " + unreadSubject);
            System.out.println("----------------------------------------------");

            WebElement firstEmail = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("tr.zA")));
            String firstEmailSubject = firstEmail.findElement(By.cssSelector("span.bog")).getText();

            WebElement checkbox = firstEmail.findElement(By.cssSelector("div[role='checkbox']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);

            WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@act='10' or @data-tooltip='Delete']")
            ));
            deleteButton.click();

            wait.until(ExpectedConditions.stalenessOf(firstEmail));

            System.out.println("LOG: Successfully deleted email: " + firstEmailSubject);

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