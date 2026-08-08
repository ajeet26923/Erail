package com.erail.support;

import com.erail.config.EnvironmentConfig;
import com.erail.config.TestEnvironment;
import com.erail.utils.ExtentReportManager;
import com.erail.utils.TestOutputCleaner;
import org.testng.SkipException;

/**
 * Central support class to switch and load environments.
 * Change environment in: src/test/resources/support/environment.properties
 */
public final class EnvironmentManager {

    private static final String ENVIRONMENT_CONFIG = "support/environment.properties";

    private EnvironmentManager() {
    }

    public static String getActiveEnvironmentName() {
        return EnvironmentConfig.getSelectedEnvironmentName();
    }

    public static boolean shouldRun(TestEnvironment environment) {
        return EnvironmentConfig.shouldRun(environment);
    }

    public static void setup(TestEnvironment environment) {
        if (!shouldRun(environment)) {
            throw new SkipException(buildSkipMessage(environment));
        }

        EnvironmentConfig.load(environment);
        if (!ExtentReportManager.isInitialized()) {
            TestOutputCleaner.cleanExistingArtifacts();
            ExtentReportManager.initializeReport();
        } else {
            TestOutputCleaner.deleteExistingScreenshots();
        }

        System.out.println("Environment loaded: " + environment.getName());
        System.out.println("Base URL: " + EnvironmentConfig.get("base.url"));
    }

    public static void switchEnvironment(String environmentName) {
        System.setProperty("environment", environmentName.trim().toLowerCase());
    }

    public static String getEnvironmentConfigPath() {
        return ENVIRONMENT_CONFIG;
    }

    private static String buildSkipMessage(TestEnvironment environment) {
        return "Skipped: active environment is '" + getActiveEnvironmentName()
                + "'. Change 'active.environment' in " + ENVIRONMENT_CONFIG
                + " to '" + environment.getName() + "' or call EnvironmentManager.switchEnvironment(\""
                + environment.getName() + "\") before setup.";
    }
}
