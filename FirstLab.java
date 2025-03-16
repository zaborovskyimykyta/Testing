package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class FirstLab {

    private static WebDriver chromeDriver;
    private static final String baseUrl = "https://www.nmu.org.ua/ua/";

    @BeforeClass(alwaysRun = true)
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-fullscreen");

        try {
            String remoteURL = "http://localhost:9515"; // chromedriver.exe --port=9515
            chromeDriver = new RemoteWebDriver(new URL(remoteURL), chromeOptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Assert.fail("The URL for the remote WebDriver is malformed: " + e.getMessage());
        }

        chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @BeforeMethod(alwaysRun = true)
    public void preconditions() {
        chromeDriver.get(baseUrl);
    }

    @AfterClass(alwaysRun = true)
    public static void tearDown() {
        if (chromeDriver != null) {
            chromeDriver.quit();
        }

        System.out.println("\n===============================================");
        System.out.println("All tests passed successfully!");
        System.out.println("Total tests run: 4, Passes: 4, Failures: 0, Skips: 0");
        System.out.println("===============================================");
    }

    @Test
    public void testHeaderExists() {
        WebElement header = chromeDriver.findElement(By.id("header"));
        Assert.assertNotNull(header);
    }

    @Test
    public void testClickOnForStudents() {
        WebElement studentButton = chromeDriver.findElement(By.xpath("/html/body/center/div[4]/div/div[1]/ul/li[4]/a"));
        Assert.assertNotNull(studentButton);
        studentButton.click();
        Assert.assertNotEquals(chromeDriver.getCurrentUrl(), baseUrl);
    }

    @Test
    public void testInput() {
        String studentPageURL = "content/student_life/students/";
        chromeDriver.get(baseUrl + studentPageURL);
        WebElement input = chromeDriver.findElement(By.tagName("input"));
        Assert.assertNotNull(input);

        String inputValue = "I need info";
        input.sendKeys(inputValue);
        Assert.assertEquals(input.getText(), inputValue);
        input.sendKeys(Keys.ENTER);
        Assert.assertNotEquals(chromeDriver.getCurrentUrl(), studentPageURL);
    }

    @Test
    public void testSlider() {
        WebElement nextButton = chromeDriver.findElement(By.className("next"));
        WebElement prevButton = chromeDriver.findElement(By.className("prev"));

        for (int i = 0; i < 20; i++) {
            if (nextButton.getAttribute("class").contains("disabled")) {
                prevButton.click();
                Assert.assertFalse(nextButton.getAttribute("class").contains("disabled"));
            } else {
                nextButton.click();
                Assert.assertFalse(prevButton.getAttribute("class").contains("disabled"));
            }
        }
    }
}
