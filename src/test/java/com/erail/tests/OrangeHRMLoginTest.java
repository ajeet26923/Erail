package com.erail.tests;

import com.aventstack.extentreports.ExtentTest;
import com.erail.base.BaseTest;
import com.erail.support.EnvironmentManager;
import com.erail.config.TestEnvironment;
import com.erail.model.LoginData;
import com.erail.pages.orangehrm.LoginPage;
import com.erail.utils.ConfigReader;
import com.erail.utils.LoginExcelUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class OrangeHRMLoginTest extends BaseTest {

    private LoginPage loginPage;
    private static List<LoginData> loginDataCache;

    @BeforeClass(alwaysRun = true)
    public void setupOrangeHrmEnvironment() throws Exception {
        EnvironmentManager.setup(TestEnvironment.ORANGEHRM);

        String loginDataFile = ConfigReader.get("login.testdata.file");
        File file = new File(loginDataFile);
        if (!file.exists()) {
            LoginExcelUtils.createDefaultLoginDataFile(loginDataFile);
        }
        loginDataCache = LoginExcelUtils.readLoginData(loginDataFile);
    }

    @BeforeMethod(alwaysRun=true)
    public void openLoginPage() {
        loginPage = new LoginPage(driver);
        String loginUrl = ConfigReader.get("base.url");
        loginPage.openLoginPage(loginUrl);
        extentTest.info("Opened OrangeHRM login page: " + loginUrl);
    }

    @DataProvider(name = "loginData")
    public Object[][] loginDataProvider() {
        if (!EnvironmentManager.shouldRun(TestEnvironment.ORANGEHRM) || loginDataCache == null) {
            return new Object[0][0];
        }

        Object[][] data = new Object[loginDataCache.size()][1];
        for (int i = 0; i < loginDataCache.size(); i++) {
            data[i][0] = loginDataCache.get(i);
        }
        return data;
    }

    @Test(dataProvider = "loginData",
            description = "Use Case 2 - Data-driven valid and invalid OrangeHRM login",groups = {"regression"})
    public void testOrangeHrmLogin(LoginData loginData) {
        ExtentTest testNode = extentTest.createNode(
                loginData.getTestCaseId() + " - " + loginData.getExpectedResult() + " Login");

        testNode.info("Username: " + loginData.getUsername());
        testNode.info("Password: " + (loginData.getPassword().isEmpty() ? "<empty>" : "****"));
        testNode.info("Expected Result: " + loginData.getExpectedResult());

        loginPage.login(loginData.getUsername(), loginData.getPassword());

        if (loginData.isValidLoginExpected()) {
            boolean loginSuccess = loginPage.isLoginSuccessful();
            if (loginSuccess) {
                testNode.pass("Valid login successful for user: " + loginData.getUsername());
            } else {
                testNode.fail("Valid login failed for user: " + loginData.getUsername());
            }
            Assert.assertTrue(loginSuccess,
                    "Expected valid login for " + loginData.getTestCaseId());
        } else {
            boolean loginFailed = loginPage.isLoginFailed();
            String errorMessage = loginPage.getErrorMessage();
            if (loginFailed) {
                testNode.pass("Invalid login correctly rejected. Message: " + errorMessage);
            } else {
                testNode.fail("Invalid login was not rejected for " + loginData.getTestCaseId());
            }
            Assert.assertTrue(loginFailed,
                    "Expected invalid login for " + loginData.getTestCaseId());
        }
    }
}
