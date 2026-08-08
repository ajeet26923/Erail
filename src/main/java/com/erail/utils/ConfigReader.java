package com.erail.utils;

import com.erail.config.EnvironmentConfig;

public final class ConfigReader {

    private ConfigReader() {
    }

    public static String get(String key) {
        return EnvironmentConfig.get(key);
    }

    public static int getInt(String key) {
        return EnvironmentConfig.getInt(key);
    }
}
