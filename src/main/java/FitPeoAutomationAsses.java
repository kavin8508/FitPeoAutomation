import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FitPeoAutomationAsses {
	private static WebDriver driver;
	private static WebDriverWait wait;
	private static final String expectedTextFieldValue = "560";
	private static final String expectedTextFieldValue_Slider = "820";
	private static final String expectedReimbursementValue = "$110700";
	private static String[] cptCodes = { "CPT-99091", "CPT-99453", "CPT-99454", "CPT-99474" };

	public static void main(String[] args) throws InterruptedException {

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--remote-allow-origins=*");
		System.setProperty("webdriver.chrome.driver",
				"/Users/kavinkumar/eclipse-workspace/FitPeoAutomation/chromedriver");
		driver = new ChromeDriver(chromeOptions);
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			// Navigate to FitPeo Homepage
			driver.get("https://www.fitpeo.com");

			// Navigate to the Revenue Calculator Page
			WebElement revenueCalculatorLink = driver
					.findElement(By.xpath("//*[contains(text(), 'Revenue Calculator')]"));
			revenueCalculatorLink.click();

			System.out.println("Current URL after click: " + driver.getCurrentUrl());
			wait.until(ExpectedConditions.urlContains("revenue-calculator"));

			// Scroll Down to the Slider section
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement sliderSection = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Medicare Eligible Patients')]")));
			js.executeScript("arguments[0].scrollIntoView(true);", sliderSection);

			// Interact with the slider input field
			WebElement sliderValueField = driver.findElement(By
					.xpath("//div[contains(@class, 'MuiFormControl-root MuiTextField-root css-1s5tg4z')]//div/input"));
			highlightElement(sliderValueField);
			Thread.sleep(1000);
			// Update the Text Field of slider
			Actions actions = new Actions(driver);

			// Clear and set the input field value
			sliderValueField.click();
			actions.click(sliderValueField)

					.keyDown(Keys.CONTROL).sendKeys("a") // Select all
					.keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE).keyDown(Keys.CONTROL).sendKeys("a") // Select all
					.keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE).keyDown(Keys.CONTROL).sendKeys("a")
					.keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE).keyDown(Keys.CONTROL).sendKeys("a") // Select all
					.keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE).sendKeys(expectedTextFieldValue).perform();
			Thread.sleep(2000);

			String actualTextFieldValue = sliderValueField.getAttribute("value");
			if (expectedTextFieldValue.equals(actualTextFieldValue)) {
				System.out.println("Input field value set correctly to " + actualTextFieldValue);
			} else {
				throw new AssertionError("Expected input field value: " + expectedTextFieldValue + ", but found: "
						+ actualTextFieldValue);
			}
			// drag the slider
			WebElement slider = driver
					.findElement(By.xpath("//*[contains(text(), 'Medicare Eligible Patients')]/../div/span"));
			// Click and drag the slider to the desired position
			int expectedSliderValue = Integer.parseInt(expectedTextFieldValue_Slider);
			WebElement ariaValueElement = driver.findElement(
					By.xpath("//*[contains(text(), 'Medicare Eligible Patients')]/../div/span/span/input"));

			for (int i = 0; i <= 2000;) {
				if (expectedSliderValue < 1000) {
					actions.clickAndHold(slider).moveByOffset(i, 0).release().perform();
					i--;
				} else if (expectedSliderValue > 1000) {
					actions.clickAndHold(slider).moveByOffset(i, 0).release().perform();
					i++;
				} else {
					actions.clickAndHold(slider).moveByOffset(i, 0).release().perform();
				}

				String ariaValueNow = ariaValueElement.getAttribute("aria-valuenow");

				if (expectedTextFieldValue_Slider.equals(ariaValueNow)) {
					System.out.println("x is equal to ariaValueNow");
					System.out.println("Aria Value Now: " + ariaValueNow);
					break;
				}
			}

			highlightElement(slider);

			// Select CPT Codes
			for (String code : cptCodes) {
				WebElement checkbox = driver
						.findElement(By.xpath("//*[contains(text(), '" + code + "')]/..//input[@type='checkbox']"));
				if (!checkbox.isSelected()) {
					checkbox.click();
					Thread.sleep(1000); // Short delay for each selection
				}
			}

			// Validate Total Recurring Reimbursement
			WebElement totalReimbursement = driver.findElement(By.xpath(
					"//*[contains(text(), 'Total Recurring Reimbursement for all Patients Per Month:')]//following-sibling::p"));
			highlightElement(totalReimbursement);
			wait.until(ExpectedConditions.textToBePresentInElement(totalReimbursement, expectedReimbursementValue));
			String actualReimbursement = totalReimbursement.getText();
			System.out.println("Actual Reimbursement: " + actualReimbursement);
			Thread.sleep(2000);

			if (actualReimbursement.contains(expectedReimbursementValue)) {
				System.out
						.println("Total Recurring Reimbursement value set correctly to " + expectedReimbursementValue);
			} else {
				throw new AssertionError("Expected Total Recurring Reimbursement: " + expectedReimbursementValue
						+ ", but found: " + actualReimbursement);
			}

			System.out.println("ALL Test Case Passed!");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error retrieving actual values: " + e.getMessage());
		} finally {
			Thread.sleep(2000);
			// Close the browser
			driver.quit();
		}
	}

	private static void highlightElement(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].style.border='3px solid yellow'", element);
	}
}