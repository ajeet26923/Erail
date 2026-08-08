package com.erail.listeners;

import com.erail.config.EnvironmentConfig;
import com.erail.utils.ExtentReportManager;
import org.testng.IClassListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestClass;

public class ExtentReportListener implements ISuiteListener, IClassListener {

    @Override
    public void onFinish(ISuite suite) {
        ExtentReportManager.flushAllReports();
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        ExtentReportManager.flushReports();
        if (EnvironmentConfig.isLoaded()) {
            System.out.println("Extent Report updated at: " + ExtentReportManager.getReportPath());
        }
    }
}
