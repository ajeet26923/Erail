package com.erail.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.erail.support.EnvironmentManager;

public final class ExtentReportManager {

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    public static boolean isInitialized() {
        return extentReports != null;
    }

    public static void initializeReport() {
        if (extentReports != null) {
            return;
        }

        TestOutputCleaner.deleteExistingReport();
        extentReports = buildExtentReports();
    }

    private static ExtentReports buildExtentReports() {
        String reportPath = ConfigReader.get("report.path");
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle(ConfigReader.get("report.title"));
        sparkReporter.config().setReportName("Automation Test Report");
        sparkReporter.config().setOfflineMode(true);

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(sparkReporter);
        reports.setSystemInfo("Environment Profile", EnvironmentManager.getActiveEnvironmentName());
        reports.setSystemInfo("Project", "Selenium Automation Framework");
        reports.setSystemInfo("Pattern", "Page Object Model");
        reports.setSystemInfo("Tool", "Selenium WebDriver + TestNG");
        return reports;
    }

    public static ExtentReports getExtentReports() {
        if (extentReports == null) {
            throw new IllegalStateException("Extent report not initialized. Call initializeReport() first.");
        }
        return extentReports;
    }

    public static void createTest(String testName) {
        EXTENT_TEST.set(getExtentReports().createTest(testName));
    }

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    public static String getReportPath() {
        return ConfigReader.get("report.path");
    }

    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    public static void flushAllReports() {
        flushReports();
    }
}
