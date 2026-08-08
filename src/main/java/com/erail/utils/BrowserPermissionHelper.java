package com.erail.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public final class BrowserPermissionHelper {

    private static final By FROM_STATION_INPUT = By.id("txtStationFrom");

    private BrowserPermissionHelper() {
    }

    /**
     * Blocks erail.in location access — equivalent to selecting "Never allow"
     * on the browser's "Know your location" permission prompt.
     */
    public static void denyLocationPermission(WebDriver driver, String origin) {
        if (!(driver instanceof ChromiumDriver)) {
            return;
        }

        ChromiumDriver chromiumDriver = (ChromiumDriver) driver;

        Map<String, Object> permission = new HashMap<>();
        permission.put("name", "geolocation");

        Map<String, Object> params = new HashMap<>();
        params.put("origin", origin);
        params.put("permission", permission);
        params.put("setting", "denied");

        chromiumDriver.executeCdpCommand("Browser.setPermission", params);
    }

    /**
     * Confirms the page is usable after denying location — the From field
     * must be visible and clickable, proving the location popup did not block the UI.
     */
    public static boolean isPageReadyAfterDenyingLocation(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement fromField = wait.until(
                    ExpectedConditions.elementToBeClickable(FROM_STATION_INPUT));
            return fromField.isDisplayed() && fromField.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
