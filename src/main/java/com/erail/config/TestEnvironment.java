package com.erail.config;

public enum TestEnvironment {

    ERAIL("erail", "config/erail.properties"),
    ORANGEHRM("orangehrm", "config/orangehrm.properties");

    private final String name;
    private final String configFile;

    TestEnvironment(String name, String configFile) {
        this.name = name;
        this.configFile = configFile;
    }

    public String getName() {
        return name;
    }

    public String getConfigFile() {
        return configFile;
    }

    public static TestEnvironment fromName(String name) {
        for (TestEnvironment environment : values()) {
            if (environment.name.equalsIgnoreCase(name)) {
                return environment;
            }
        }
        throw new IllegalArgumentException("Unsupported environment: " + name);
    }
}
