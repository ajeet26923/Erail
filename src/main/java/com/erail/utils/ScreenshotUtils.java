package com.erail.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public final class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    public static void attachScreenshotToReport(WebDriver driver, ExtentTest extentTest, ITestResult result) {
        if (driver == null || extentTest == null) {
            return;
        }

        try {
            TestOutputCleaner.deleteExistingScreenshots();

            String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            saveLatestScreenshot(base64);

            String statusLabel = getStatusLabel(result.getStatus());
            String message = statusLabel + " - Screenshot attached to Extent Report";

            if (result.getStatus() == ITestResult.SUCCESS) {
                extentTest.pass(message,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            } else if (result.getStatus() == ITestResult.FAILURE) {
                extentTest.fail(message,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            } else if (result.getStatus() == ITestResult.SKIP) {
                extentTest.skip(message,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            }
        } catch (Exception e) {
            extentTest.warning("Unable to attach screenshot to Extent Report: " + e.getMessage());
        }
    }

    private static void saveLatestScreenshot(String base64) throws Exception {
        Path destination = Paths.get(ConfigReader.get("screenshot.path"));
        Files.createDirectories(destination.getParent());
        Files.write(destination, Base64.getDecoder().decode(base64));
    }

    private static String getStatusLabel(int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                return "PASS";
            case ITestResult.FAILURE:
                return "FAIL";
            case ITestResult.SKIP:
                return "SKIP";
            default:
                return "UNKNOWN";
        }
    }
}
