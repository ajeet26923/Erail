# Selenium Automation Framework

Test automation framework built with **Java**, **Maven**, **Selenium WebDriver**, **TestNG**, **Page Object Model (POM)**, **Apache POI**, and **Extent Reports**.

Covers two use cases:
- **Use Case 1:** [eRail.in](https://erail.in/) station search
- **Use Case 2:** [OrangeHRM](https://opensource-demo.orangehrmlive.com/) data-driven login

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 11 |
| Build Tool | Maven |
| Automation | Selenium WebDriver 4 |
| Test Framework | TestNG |
| Design Pattern | Page Object Model |
| Reporting | Extent Reports 5 |
| Excel | Apache POI |
| Driver Management | WebDriverManager |

## Project Structure

```
src/main/java/com/erail/
├── base/
│   ├── BaseTest.java              # Common setup/teardown
│   ├── ErailBaseTest.java         # eRail environment setup
│   ├── OrangeHrmBaseTest.java     # OrangeHRM environment setup
│   └── DriverManager.java
├── support/
│   └── EnvironmentManager.java    # Switch/load environment (call from tests)
├── config/
│   ├── TestEnvironment.java
│   └── EnvironmentConfig.java
├── pages/
│   ├── BasePage.java              # Shared PageFactory base for all pages
│   ├── erail/
│   │   └── HomePage.java          # Use Case 1 - eRail POM
│   └── orangehrm/
│       └── LoginPage.java         # Use Case 2 - OrangeHRM POM
├── model/
│   └── LoginData.java             # Login test data model
└── utils/
    ├── ConfigReader.java
    ├── ExcelUtils.java
    ├── LoginExcelUtils.java       # Login Excel data-driven utils
    └── ExtentReportManager.java

src/test/java/com/erail/tests/
├── ErailSearchTest.java           # Use Case 1
└── OrangeHRMLoginTest.java        # Use Case 2

src/test/resources/
├── support/
│   └── environment.properties     # CHANGE ENVIRONMENT HERE
└── config/
    ├── global.properties          # Shared settings (browser, waits)
    ├── erail.properties           # Use Case 1 environment
    └── orangehrm.properties       # Use Case 2 environment
```

## Use Case 1 Coverage

1. Initialize WebDriver and open `https://erail.in/`
2. Click **From** input field
3. Clear existing station data
4. Type **DEL** to open autocomplete dropdown
5. Select **4th station** and print name to console
6. Create Excel file with expected station names
7. Capture dropdown list, write to Excel, compare with expected
8. Select **Sort on Date** and pick date **30 days ahead** (dynamic)
9. Generate **Extent Report**

## Use Case 2 Coverage

1. Open OrangeHRM login: `https://opensource-demo.orangehrmlive.com/web/index.php/auth/login`
2. **Data-driven testing** from Excel (`LoginTestData.xlsx`) with valid and invalid credentials
3. Generate **Extent Report** with pass/fail for each login scenario

### Login Test Data (Excel)

| TestCaseID | Username    | Password   | ExpectedResult |
|------------|-------------|------------|----------------|
| TC001      | Admin       | admin123   | Valid          |
| TC002      | Admin       | wrongpass  | Invalid        |
| TC003      | invaliduser | admin123   | Invalid        |
| TC004      | Admin       | *(empty)*  | Invalid        |
| TC005      | *(empty)*   | admin123   | Invalid        |

Excel file is auto-created at `src/test/resources/testdata/LoginTestData.xlsx` if missing.

## Prerequisites

- Java JDK 11+
- Maven 3.6+
- Chrome browser (default)

## Manual Environment Switch

Open **`src/test/resources/support/environment.properties`** and change:

```properties
active.environment=erail
```

| Value | Runs |
|-------|------|
| `erail` | Use Case 1 only (eRail.in) |
| `orangehrm` | Use Case 2 only (OrangeHRM) |
| `all` | Both use cases |

Each test class calls **`EnvironmentManager.setup()`** in `@BeforeClass`:

```java
// ErailSearchTest.java
EnvironmentManager.setup(TestEnvironment.ERAIL);

// OrangeHRMLoginTest.java
EnvironmentManager.setup(TestEnvironment.ORANGEHRM);
```

Then run:

```bash
mvn clean test
```

### Optional command-line override

You can override the file setting without editing the file:

```bash
mvn clean test -Denvironment=orangehrm
```

Or use Maven profiles:

```bash
mvn clean test -Perail
mvn clean test -Porangehrm
mvn clean test -Pall
```

> **Note:** `-Denvironment=...` and Maven profiles override `active.environment` in `global.properties`.

## Reports & Output

| Output | Location |
|--------|----------|
| **Extent Report (both use cases)** | `test-output/extentReport/ExtentReport.html` |
| eRail screenshot | `test-output/erail/LatestScreenshot.png` |
| eRail actual stations Excel | `test-output/erail/ActualStations.xlsx` |
| OrangeHRM screenshot | `test-output/orangehrm/LatestScreenshot.png` |

| Shared Test Data | Location |
|------------------|----------|
| Expected Stations Excel | `src/test/resources/testdata/ExpectedStations.xlsx` |
| Login Test Data Excel | `src/test/resources/testdata/LoginTestData.xlsx` |

## Configuration

Each environment has its own property file under `src/test/resources/config/`:

**`global.properties`**
```properties
report.path=test-output/extentReport/ExtentReport.html
report.title=Functional Testing Assignment Report
```

**`erail.properties`**
```properties
environment.name=eRail
base.url=https://erail.in/
```

**`orangehrm.properties`**
```properties
environment.name=OrangeHRM
base.url=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
```

Shared browser/wait settings are in `config/global.properties`.
