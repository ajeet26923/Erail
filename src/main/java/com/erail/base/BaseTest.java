package com.erail.base;

import com.erail.config.EnvironmentConfig;
import com.erail.listeners.ExtentReportListener;
import com.erail.utils.ConfigReader;
import com.erail.utils.ExtentReportManager;
import com.erail.utils.ScreenshotUtils;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(ExtentReportListener.class)
public abstract class BaseTest {

    protected WebDriver driver;
    protected ExtentTest extentTest;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        if (!EnvironmentConfig.isLoaded()) {
            return;
        }

        ExtentReportManager.createTest(getClass().getSimpleName());
        extentTest = ExtentReportManager.getTest();
        extentTest.info("Active environment: " + ConfigReader.get("environment.name"));

        driver = DriverManager.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(org.testng.ITestResult result) {
        if (!EnvironmentConfig.isLoaded() || driver == null) {
            return;
        }

        ScreenshotUtils.attachScreenshotToReport(driver, extentTest, result);
        ExtentReportManager.flushReports();
        DriverManager.quitDriver();
        driver = null;
    }

    @AfterSuite(alwaysRun = true)
    public void flushExtentReport() {
        ExtentReportManager.flushAllReports();
        if (EnvironmentConfig.isLoaded()) {
            System.out.println("Extent Report generated at: " + ExtentReportManager.getReportPath());
        }
    }
}
