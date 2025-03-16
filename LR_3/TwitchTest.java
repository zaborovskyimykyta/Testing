package LR_3;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class TwitchTest {

    private static WebDriver driver;
    private static final String BASE_URL = "https://www.twitch.tv/";
    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    @BeforeClass(alwaysRun = true)
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        try {
            String remoteURL = "http://localhost:9515";
            driver = new RemoteWebDriver(new URL(remoteURL), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Remote WebDriver URL", e);
        }

        driver.manage().timeouts().implicitlyWait(TIMEOUT);
    }

    @BeforeMethod(alwaysRun = true)
    public void preconditions() {
        driver.get(BASE_URL);
    }

    @AfterClass(alwaysRun = true)
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testHeaderExists() {
        boolean isHeaderPresent = new WebDriverWait(driver, TIMEOUT)
                .until(driver -> driver.findElements(By.tagName("header")).size() > 0);
        Assert.assertTrue(isHeaderPresent, "Элемент <header> отсутствует на странице.");
    }

    @Test
    public void testClickOnBrowse() {
        WebElement browseButton = new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/directory']")));

        browseButton.click();

        boolean urlChanged = new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.not(ExpectedConditions.urlToBe(BASE_URL)));

        Assert.assertTrue(urlChanged, "URL не изменился после клика на 'Browse'.");
    }

    @Test
    public void testSearch() {
        WebElement searchInput = new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='search']")));

        Assert.assertNotNull(searchInput);

        String searchQuery = "Valorant";
        searchInput.sendKeys(searchQuery);
        searchInput.sendKeys(Keys.ENTER);

        boolean urlChanged = new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.not(ExpectedConditions.urlToBe(BASE_URL)));

        Assert.assertTrue(urlChanged, "URL не изменился после поиска.");
    }

    @Test
    public void testStreamClick() {
        WebElement firstStream = new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[contains(@href, '/videos/')])[1]")));

        firstStream.click();

        boolean urlChanged = new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.not(ExpectedConditions.urlToBe(BASE_URL)));

        Assert.assertTrue(urlChanged, "URL не изменился после клика на первый стрим.");
    }
}
