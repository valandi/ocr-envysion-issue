package com.applitools.example;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.locators.OcrRegion;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import java.io.File;
import java.net.URISyntaxException;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class Demo {

	private static WebDriver driver;
	private static final EyesRunner runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
	private static final Eyes eyes = new Eyes(runner);

	public static void main(String[] args) {
		try {
			configureEyes();
			setupDriver();
			navigateToLocalFile();

			performOcrTest();
			performVisualCheck("table");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tearDown();
		}
	}

	private static void navigateToLocalFile() {
		String basePath = null;
		try {
			basePath = new File(Demo.class.getClassLoader().getResource("Eclypse.html").toURI()).getAbsolutePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		driver.get("file:///" + basePath);
	}

	private static void setupDriver() {
		driver = new ChromeDriver();
		eyes.open(driver, "Ecylpse test", "Eclypse test", new RectangleSize(1000, 1000));
	}

	private static void configureEyes() {
		Configuration config = new Configuration()
			.addBrowser(1000, 1000, BrowserType.CHROME)
			.setBatch(new BatchInfo("Eclypse Test"));
		eyes.setConfiguration(config);
	}

	private static void performOcrTest() {
		WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
			.until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector("#gridAudit > div > div.vaadin-grid.style-scope.vaadin-grid-tablewrapper > table > tbody > tr:nth-child(9) > td:nth-child(2)")
			));

		List<String> textsFound = eyes.extractText(new OcrRegion(element).hint("ENVYSION"));
		textsFound.forEach(text -> System.out.println("Found: " + text));
	}

	private static void performVisualCheck(String tag) {
		eyes.check(tag, Target.window().fully(true));
		eyes.closeAsync();
	}

	private static void tearDown() {
		try {
			driver.quit();
			TestResultsSummary allTestResults = runner.getAllTestResults(false);
			System.out.println(allTestResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}