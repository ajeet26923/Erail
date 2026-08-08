package com.erail.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class EnvironmentConfig {

    private static final String GLOBAL_CONFIG = "config/global.properties";
    private static final String ENVIRONMENT_SWITCH_CONFIG = "support/environment.properties";
    private static final String ACTIVE_ENV_KEY = "active.environment";
    private static final String SYSTEM_ENV_KEY = "environment";

    private static final Properties PROPERTIES = new Properties();
    private static TestEnvironment activeEnvironment;

    private EnvironmentConfig() {
    }

    public static void load(TestEnvironment environment) {
        PROPERTIES.clear();
        activeEnvironment = environment;
        loadProperties(GLOBAL_CONFIG);
        loadProperties(environment.getConfigFile());
    }

    public static String getSelectedEnvironmentName() {
        String fileEnvironment = readProperty(
                ENVIRONMENT_SWITCH_CONFIG, ACTIVE_ENV_KEY, TestEnvironment.ERAIL.getName());
        String systemEnvironment = System.getProperty(SYSTEM_ENV_KEY);

        if (isExplicitEnvironmentOverride(systemEnvironment)) {
            return systemEnvironment.trim().toLowerCase();
        }

        return fileEnvironment.trim().toLowerCase();
    }

    private static boolean isExplicitEnvironmentOverride(String environment) {
        if (environment == null || environment.isBlank() || environment.startsWith("${")) {
            return false;
        }

        String normalized = environment.trim().toLowerCase();
        return TestEnvironment.ERAIL.getName().equals(normalized)
                || TestEnvironment.ORANGEHRM.getName().equals(normalized)
                || "all".equals(normalized);
    }

    public static boolean shouldRun(TestEnvironment environment) {
        String selected = getSelectedEnvironmentName();
        return "all".equals(selected) || environment.getName().equals(selected);
    }

    private static String readProperty(String fileName, String key, String defaultValue) {
        Properties properties = new Properties();
        try (InputStream input = EnvironmentConfig.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read configuration file: " + fileName, e);
        }
        return properties.getProperty(key, defaultValue);
    }

    private static void loadProperties(String fileName) {
        try (InputStream input = EnvironmentConfig.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + fileName);
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration file: " + fileName, e);
        }
    }

    public static TestEnvironment getActiveEnvironment() {
        if (activeEnvironment == null) {
            throw new IllegalStateException("Environment is not loaded. Call EnvironmentManager.setup() first.");
        }
        return activeEnvironment;
    }

    public static boolean isLoaded() {
        return activeEnvironment != null;
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(PROPERTIES.getProperty(key));
    }
}
