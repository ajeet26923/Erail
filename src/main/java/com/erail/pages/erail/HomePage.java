package com.erail.pages.erail;

import com.erail.pages.BasePage;
import com.erail.utils.BrowserPermissionHelper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HomePage extends BasePage {

    @FindBy(id = "txtStationFrom")
    private WebElement fromStationInput;

    @FindBy(xpath = "//div[@class='autocomplete-w1']//div[@title]")
    private List<WebElement> stationSuggestions;

    @FindBy(css = "#tdDateFromTo input[type='button']")
    private WebElement dateButton;

    @FindBy(id = "chkSelectDateOnly")
    private WebElement sortOnDateCheckbox;

    @FindBy(id = "divCalender")
    private WebElement calendarContainer;

    public HomePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void openHomePage(String url) {
        BrowserPermissionHelper.denyLocationPermission(driver, url);
        openPage(url);
        refreshElements();
        wait.until(ExpectedConditions.visibilityOf(fromStationInput));
    }

    public void prepareFromField() {
        refreshElements();
        wait.until(ExpectedConditions.elementToBeClickable(fromStationInput));
        fromStationInput.click();
        fromStationInput.clear();
    }

    public void typeInFromField(String text) {
        refreshElements();
        fromStationInput.sendKeys(text);
        waitForSuggestions(1);
    }

    public List<String> getDropdownStationNames() {
        waitForSuggestions(1);
        return getSuggestionNames();
    }

    public String selectStationByIndex(int index) {
        waitForSuggestions(index + 1);
        refreshElements();
        String stationName = stationSuggestions.get(index).getAttribute("title").trim();
        stationSuggestions.get(index).click();
        return stationName;
    }

    public void enableSortOnDate() {
        refreshElements();
        wait.until(ExpectedConditions.elementToBeClickable(sortOnDateCheckbox));
        if (!sortOnDateCheckbox.isSelected()) {
            sortOnDateCheckbox.click();
        }
    }

    public void selectDateAfterDays(int days) {
        LocalDate targetDate = LocalDate.now().plusDays(days);
        long timestamp = targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        refreshElements();
        wait.until(ExpectedConditions.elementToBeClickable(dateButton));
        dateButton.click();
        wait.until(ExpectedConditions.visibilityOf(calendarContainer));

        ((JavascriptExecutor) driver).executeScript("DoDateSelect(arguments[0]);", timestamp);

        wait.until(ExpectedConditions.invisibilityOf(calendarContainer));
    }

    public String getDisplayedDateValue() {
        refreshElements();
        wait.until(ExpectedConditions.visibilityOf(dateButton));
        return dateButton.getAttribute("value");
    }

    public static List<String> defaultExpectedStationsForDelSearch() {
        return Arrays.asList(
                "Denduluru", "Delang", "Delhi",
                "Delhi Azadpur", "Delhi Cantt", "Delhi Indrapuri");
    }

    private void waitForSuggestions(int minimumCount) {
        wait.until(driver -> {
            refreshElements();
            return stationSuggestions.size() >= minimumCount;
        });
    }

    private List<String> getSuggestionNames() {
        refreshElements();
        return stationSuggestions.stream()
                .map(station -> station.getAttribute("title").trim())
                .collect(Collectors.toList());
    }
}
