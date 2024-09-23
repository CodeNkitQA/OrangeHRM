import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Google {

    public static void main(String[] args) throws InterruptedException, IOException {
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().browserVersion("129.0.0.0").setup();

        // Initialize Chrome WebDriver
        WebDriver driver = new ChromeDriver();

        // Maximize the browser window
        driver.manage().window().maximize();

        // Navigate to Google Maps
        driver.get("https://www.google.com/maps");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@id='searchboxinput'])[1]")));
        search.click();
        search.sendKeys("Hair Saloon near TX, USA");
        search.sendKeys(Keys.ENTER);

        // Wait for search results to load
        Thread.sleep(5000);

        // Create Excel workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Google Map Data");

        // Create header row in the Excel sheet
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Title");
        headerRow.createCell(1).setCellValue("Location");
        headerRow.createCell(2).setCellValue("Website");
        headerRow.createCell(3).setCellValue("Phone");

        int rowIndex = 1;  // Row index for the Excel sheet
        int processedTabs = 0;  // Keep track of how many tabs we've processed

        // Open the file output stream once, to append data as it's fetched
        try (FileOutputStream fileOut = new FileOutputStream("/Users/ankitkumar/Downloads/Outputsheet.xlsx")) {

            // Loop to handle dynamic loading of tabs and fetching their details
            while (processedTabs < 200) {  // Adjust this limit as needed
                // Get the list of tabs visible at the moment
                List<WebElement> tabs = driver.findElements(By.className("hfpxzc"));

                for (int i = processedTabs; i < tabs.size(); i++) {
                    boolean success = false;
                    int attempts = 0;

                    // Retry mechanism to handle potential StaleElementReferenceException
                    while (attempts < 3) {
                        try {
                            // Re-locate the tab before interacting with it
                            WebElement tab = driver.findElements(By.className("hfpxzc")).get(i);

                            // Scroll to the tab before clicking
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tab);
                            wait.until(ExpectedConditions.elementToBeClickable(tab)).click();

                            Thread.sleep(2000);  // Adjust wait time based on network speed

                            // Fetch title and location
                            String title = getTextByClassName(driver, ".DUwDvf.lfPIob");  // Title
                            String location = getTextByClassName1(driver, "(//div[contains(@class,'rogA2c')])[1]");

                            // Check if the website element exists
                            String website;
                            try {
                                WebElement websiteElement = driver.findElement(By.xpath("(//div[@class='rogA2c ITvuef'])[1]"));
                                website = websiteElement.getText();
                            } catch (NoSuchElementException e) {
                                website = "N/A";  // If website element doesn't exist
                            }

                            // Fetch phone number (handle missing element gracefully)
                            String phoneText;
                            try {
                                WebElement phone = driver.findElement(By.xpath("(//div[contains(@class,'rogA2c')])[4]"));
                                phoneText = phone.getText();
                            } catch (NoSuchElementException e) {
                                phoneText = "N/A";  // If phone number element doesn't exist
                            }

                            // Print data to the console (for debugging)
                            System.out.println("Title: " + title);
                            System.out.println("Location: " + location);
                            System.out.println("Website: " + website);
                            System.out.println("Phone: " + phoneText);
                            System.out.println("----------------------------");

                            // Write data to the Excel sheet immediately after fetching it
                            Row row = sheet.createRow(rowIndex++);
                            row.createCell(0).setCellValue(title);
                            row.createCell(1).setCellValue(location);
                            row.createCell(2).setCellValue(website);
                            row.createCell(3).setCellValue(phoneText);

                            // Write the current data to the file to ensure nothing is lost
                            workbook.write(fileOut);

                            Thread.sleep(2000);  // Wait for 2 seconds after fetching data

                            // Mark success if no exception occurs
                            success = true;
                            break;  // Break out of retry loop
                        } catch (StaleElementReferenceException e) {
                            attempts++;  // Retry if stale element exception occurs
                        }
                    }

                    if (!success) {
                        System.out.println("Failed to interact with element after 3 attempts");
                    }
                }

                // Update the processed tabs counter
                processedTabs = tabs.size();

                // Scroll to load more results
                WebElement lastTab = driver.findElements(By.className("hfpxzc")).get(tabs.size() - 1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", lastTab);
                Thread.sleep(3000);  // Wait for new results to load
            }
        }

        // Close the workbook and the browser
        workbook.close();
        driver.quit();
    }

    // Helper method to get text by class name (CSS selector)
    public static String getTextByClassName(WebDriver driver, String className) {
        try {
            WebElement element = driver.findElement(By.cssSelector(className));
            return element.getText();
        } catch (Exception e) {
            return "N/A";  // Return "N/A" if the element is not found
        }
    }

    // Helper method to get text by XPath
    public static String getTextByClassName1(WebDriver driver, String className) {
        try {
            WebElement element = driver.findElement(By.xpath(className));
            return element.getText();
        } catch (Exception e) {
            return "N/A";  // Return "N/A" if the element is not found
        }
    }
}








//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.time.Duration;
//import java.util.List;
//
//public class Google {
//
//    public static void main(String[] args) throws InterruptedException, IOException {
//        // Setup ChromeDriver using WebDriverManager
//        WebDriverManager.chromedriver().browserVersion("129.0.0.0").setup();
//
//        // Initialize Chrome WebDriver
//        WebDriver driver = new ChromeDriver();
//
//        // Maximize the browser window
//        driver.manage().window().maximize();
//
//        // Navigate to Google Maps
//        driver.get("https://www.google.com/maps");
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//
//        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@id='searchboxinput'])[1]")));
//        search.click();
//        search.sendKeys("Hair Saloon near TX, USA");
//        search.sendKeys(Keys.ENTER);
//
//        // Wait for search results to load
//        Thread.sleep(5000);
//
//        int rowIndex = 1;
//
//        int processedTabs = 0;  // Keep track of how many tabs we've processed
//
//        // Loop to handle dynamic loading of tabs and fetching their details
//        while (processedTabs < 200) {  // Adjust this limit as needed
//            // Get the list of tabs visible at the moment
//            List<WebElement> tabs = driver.findElements(By.className("hfpxzc"));
//
//            for (int i = processedTabs; i < tabs.size(); i++) {
//                WebElement tab = tabs.get(i);
//
//                // Scroll to the tab before clicking
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tab);
//                wait.until(ExpectedConditions.elementToBeClickable(tab)).click();
//
//                Thread.sleep(2000);  // Adjust wait time based on network speed
//
//                // Fetch title and location
//                String title = getTextByClassName(driver, ".DUwDvf.lfPIob");  // Title
//                String location = getTextByXPath(driver, "(//div[contains(@class,'rogA2c')])[1]");
//
//                // Check if the website element exists
//                String website = getTextByXPath(driver, "(//div[@class='rogA2c ITvuef'])[1]");
//
//                // Fetch phone number (handle missing element gracefully)
//                String phoneText = getTextByXPath(driver, "(//div[contains(@class,'rogA2c')])[4]");
//                System.out.println("Title: " + title);
//                System.out.println("Location: " + location);
//                System.out.println("Website: " + website);
//                System.out.println("Phone: " + phoneText);
//                System.out.println("----------------------------");
//
//                // Write data to the Excel sheet
//
//                Thread.sleep(1000);  // Wait for 2 seconds after fetching data
//            }
//
//            // Update the processed tabs counter
//            processedTabs = tabs.size();
//
//            // Scroll to load more results
//            if (!tabs.isEmpty()) {
//                WebElement lastTab = tabs.get(tabs.size() - 1);
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", lastTab);
//                Thread.sleep(3000);  // Wait for new results to load
//            }
//        }
//
//
//        // Close the browser
//        driver.quit();
//    }
//
//    // Helper method to get text by class name (CSS selector)
//    public static String getTextByClassName(WebDriver driver, String className) {
//        try {
//            WebElement element = driver.findElement(By.cssSelector(className));
//            return element.getText();
//        } catch (Exception e) {
//            return "N/A";  // Return "N/A" if the element is not found
//        }
//    }
//
//    // Helper method to get text by XPath
//    public static String getTextByXPath(WebDriver driver, String xpath) {
//        try {
//            WebElement element = driver.findElement(By.xpath(xpath));
//            return element.getText();
//        } catch (Exception e) {
//            return "N/A";  // Return "N/A" if the element is not found
//        }
//    }
//}
