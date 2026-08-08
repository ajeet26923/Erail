package com.erail.utils;

import java.io.File;

public final class TestOutputCleaner {

    private TestOutputCleaner() {
    }

    public static void cleanExistingArtifacts() {
        deleteExistingReport();
        deleteExistingScreenshots();
    }

    public static void deleteExistingReport() {
        File reportFile = new File(ConfigReader.get("report.path"));
        if (reportFile.exists()) {
            reportFile.delete();
        }
    }

    public static void deleteExistingScreenshots() {
        File screenshotFile = new File(ConfigReader.get("screenshot.path"));
        if (screenshotFile.exists()) {
            screenshotFile.delete();
        }

        File screenshotDir = screenshotFile.getParentFile();
        if (screenshotDir != null && screenshotDir.exists()) {
            deletePngFiles(screenshotDir);
        }
    }

    private static void deletePngFiles(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                deletePngFiles(file);
            } else if (file.getName().toLowerCase().endsWith(".png")) {
                file.delete();
            }
        }
    }
}
