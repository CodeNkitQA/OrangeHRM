import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OrangeHRMTest {

    public static void main(String[] args) throws IOException {
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.manage().window().maximize();

            // Read data from Excel
            String filePath = "/Users/ankitkumar/Downloads/Orangehrm/Assignment.xlsx";
            String sheetName = "Assignment";
            Object[][] loginData = readExcelData(filePath, sheetName);

            // Iterate through each row of test data
            for (Object[] row : loginData) {
                String username = row[0].toString();
                String password = row[1].toString();
                String expectedResult = row[2].toString();

                // Perform login test
                driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

                try {
                    // Locate elements
                    WebElement usernameField = driver.findElement(By.xpath("//input[@placeholder='Username']"));
                    WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));
                    WebElement loginButton = driver.findElement(By.xpath("//button[normalize-space()='Login']"));

                    // Perform login
                    usernameField.clear();
                    usernameField.sendKeys(username);
                    passwordField.clear();
                    passwordField.sendKeys(password);
                    loginButton.click();

                    // Check for dashboard element
                    driver.findElement(By.xpath("//h6[contains(text(),'Dashboard')]"));
                    System.out.println("Test passed for: " + username);

                } catch (Exception e) {
                    System.out.println("Test failed for: " + username + ". Expected: " + expectedResult + ", but got: Failure");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            if (driver != null) {
                driver.quit();
            }
        }
    }

    // Mocked method to read data from Excel (to be implemented)
    public static Object[][] readExcelData(String filePath, String sheetName) {
        // Implement Excel reading logic here
        return new Object[][] {
                {"Admin", "admin123", "Success"},
                {"InvalidUser", "invalidPass", "Failure"}
        };
    }
}
