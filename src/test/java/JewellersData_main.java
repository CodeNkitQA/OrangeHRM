import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JewellersData_main {

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        try {
            driver.get("https://www.google.com/");
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("jewellers contact us", Keys.ENTER);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            Set<String> visitedUrls = new HashSet<>();
            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
            Set<String> foundEmails = new HashSet<>();

            // Define excluded store names
            Set<String> excludedStores = Set.of(
                    "Bluestone Jewellery", "Giri Zever Mahal", "ZOYA", "Tanishq Jewellery",
                    "PC Chandra Jewellers", "Caratlane", "Kalyan Jewellers",
                    "Malabar Gold and Diamonds", "Senco Gold and Diamonds", "GIVA"
            );

            while (true) {
                // Locate all relevant links in search results using broad XPath
                List<WebElement> links = driver.findElements(By.xpath("//a[contains(@href, 'http')]"));

                for (WebElement link : links) {
                    String linkUrl = link.getAttribute("href");

                    if (linkUrl == null || visitedUrls.contains(linkUrl)) continue;
                    visitedUrls.add(linkUrl);

                    try {
                        // Open link in a new tab
                        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0])", linkUrl);
                        String originalWindow = driver.getWindowHandle();

                        // Switch to new tab
                        for (String windowHandle : driver.getWindowHandles()) {
                            if (!windowHandle.equals(originalWindow)) {
                                driver.switchTo().window(windowHandle);
                                break;
                            }
                        }

                        // Wait for the page to load completely
                        wait.until(driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete"));
                        String pageTitle = driver.getTitle();
                        System.out.println("Page Title: " + pageTitle);

                        // Skip fetching if the page title contains any excluded store name
                        if (excludedStores.stream().anyMatch(pageTitle::contains)) {
                            System.out.println("Skipping excluded store: " + pageTitle);
                            driver.close();
                            driver.switchTo().window(originalWindow);
                            continue;
                        }

                        // Fetch page source and search for unique emails
                        String pageSource = driver.getPageSource();
                        Matcher matcher = emailPattern.matcher(pageSource);
                        while (matcher.find()) {
                            String email = matcher.group();
                            if (foundEmails.add(email)) { // Print only if the email is new
                                System.out.println("Email found: " + email);
                            }
                        }

                        // Print separator after processing emails from one website
                        System.out.println("------------------------");

                        driver.close();  // Close the current tab
                        driver.switchTo().window(originalWindow);  // Switch back to original tab
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
                    } catch (TimeoutException e) {
                        System.out.println("Error: Page load timed out for URL - " + linkUrl);
                        driver.close();
                        driver.switchTo().window(driver.getWindowHandle());
                    } catch (WebDriverException e) {
                        System.out.println("Error processing link: " + e.getMessage());
                        driver.navigate().back();
                    }
                }

                // Navigate to the next page in search results
                try {
                    WebElement nextButton = driver.findElement(By.xpath("//a[@id='pnnext']"));
                    nextButton.click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
                } catch (NoSuchElementException e) {
                    System.out.println("No more pages to navigate.");
                    break;
                }
            }
        } finally {
            driver.quit();
        }
    }
}
