package com.scarletra;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;

public class GmailTest {
    WebDriver driver;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testGmailDeletion() throws InterruptedException {
        driver.get("https://mail.google.com/mail/");

        driver.findElement(By.id("identifierId")).sendKeys("anonym100205@gmail.com");
        driver.findElement(By.id("identifierNext")).click();

        Thread.sleep(3000); 

        driver.findElement(By.name("Passwd")).sendKeys("Scarletra05");
        driver.findElement(By.id("passwordNext")).click();

        List<WebElement> unreadEmails = driver.findElements(By.cssSelector("tr.zE"));

        if (!unreadEmails.isEmpty()) {
            WebElement lastUnread = unreadEmails.get(0);
            String subject = lastUnread.findElement(By.cssSelector("span.bog")).getText();
            System.out.println("LOG: Title email unread terakhir adalah -> " + subject);
        } else {
            System.out.println("LOG: Tidak ada email unread.");
        }
    }

    @AfterMethod
    public void tearDown() {
        // driver.quit();
    }
}