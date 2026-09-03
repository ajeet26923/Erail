package com.erail.tests;

import com.erail.base.BaseTest;
import com.erail.support.EnvironmentManager;
import com.erail.config.TestEnvironment;
import com.erail.pages.erail.HomePage;
import com.erail.utils.ConfigReader;
import com.erail.utils.ExcelUtils;
import com.erail.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ErailSearchTest extends BaseTest {

	private HomePage homePage;

	@BeforeClass(alwaysRun = true)
	public void setupErailEnvironment() {
		EnvironmentManager.setup(TestEnvironment.ERAIL);
	}

	@BeforeMethod
	public void openErailApplication() {
		homePage = new HomePage(driver);
		String baseUrl = ConfigReader.get("base.url");
		homePage.openHomePage(baseUrl);
		extentTest.info("Navigated to: " + baseUrl);
	}

	@Test(description = "Use Case 1 - eRail station search, Excel validation, and date selection")
	public void testErailStationSearchUseCase() throws Exception {
		Map<String, String> dataSet = JsonUtils.readTestDataSetByCaseName(ConfigReader.get("search.testdata.file"),
				"testErailStationSearchUseCase");

		homePage.prepareFromField();
		extentTest.info("Step 2 & 3: Clicked and cleared From input field");

		String searchText = dataSet.get("searchText");
		homePage.typeInFromField(searchText);
		extentTest.info("Step 4: Typed '" + searchText + "' in From field");

		int stationIndex = Integer.parseInt(dataSet.get("fourthStationPosition"));
		List<String> actualStations = homePage.getDropdownStationNames();
		String fourthStation = homePage.selectStationByIndex(stationIndex);

		System.out.println("4th Station Selected: " + fourthStation);
		extentTest.pass("Step 5: Selected 4th station from dropdown: " + fourthStation);

		String expectedFilePath = ConfigReader.get("expected.stations.file");
		List<String> expectedStations = HomePage.defaultExpectedStationsForDelSearch();
		ExcelUtils.createExpectedStationsFile(expectedFilePath, expectedStations);
		extentTest.info("Step 6: Created expected stations Excel at " + expectedFilePath);

		String actualFilePath = ConfigReader.get("actual.stations.file");
		ExcelUtils.writeActualStationsFile(actualFilePath, actualStations);
		extentTest.info("Step 7: Wrote actual dropdown stations to " + actualFilePath);

		List<String> expectedFromExcel = ExcelUtils.readStationNames(expectedFilePath);
		boolean stationsMatch = ExcelUtils.compareStationLists(expectedFromExcel, actualStations);

		extentTest.info("Expected stations: " + expectedFromExcel);
		extentTest.info("Actual stations: " + actualStations);

		if (stationsMatch) {
			extentTest.pass("Step 7: Expected stations match the first " + expectedFromExcel.size()
					+ " items in the actual dropdown list");
		} else {
			extentTest.fail("Step 7: Expected stations do not match the dropdown prefix");
		}

		Assert.assertTrue(stationsMatch, "Expected stations should match the first entries in the dropdown list");

		homePage.enableSortOnDate();
		int daysAhead = Integer.parseInt(dataSet.get("daysAhead"));
		homePage.selectDateAfterDays(daysAhead);

		LocalDate expectedDate = LocalDate.now().plusDays(daysAhead);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy EEE", Locale.ENGLISH);
		String expectedDateLabel = expectedDate.format(formatter);

		String selectedDate = homePage.getDisplayedDateValue();
		extentTest.info("Step 8: Selected date displayed as: " + selectedDate);
		extentTest.pass("Step 8: Dynamically selected date " + daysAhead + " days ahead (" + expectedDateLabel + ")");

		Assert.assertTrue(
				selectedDate.toLowerCase().contains(
						expectedDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH)).toLowerCase()),
				"Selected date should reflect " + daysAhead + " days from today. Expected around: " + expectedDateLabel
						+ ", but was: " + selectedDate);
		Assert.fail();
		extentTest.pass("Step 9: Test execution logged to Extent Report");
	}
}
