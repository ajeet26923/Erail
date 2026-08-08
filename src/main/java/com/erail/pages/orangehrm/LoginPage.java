package com.erail.pages.orangehrm;

import com.erail.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    private static final By ERROR_MESSAGE = By.cssSelector(".oxd-alert-content-text");

    @FindBy(xpath = "//input[@name='username']")
    private WebElement usernameInput;

    @FindBy(xpath = "//input[@name='password']")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void openLoginPage(String url) {
        openPage(url);
        refreshElements();
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
    }

    public void login(String username, String password) {
        refreshElements();
        wait.until(ExpectedConditions.visibilityOf(usernameInput));

        usernameInput.clear();
        passwordInput.clear();

        if (username != null && !username.isEmpty()) {
            usernameInput.sendKeys(username);
        }
        if (password != null && !password.isEmpty()) {
            passwordInput.sendKeys(password);
        }

        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }

    public boolean isLoginSuccessful() {
        try {
            wait.until(ExpectedConditions.urlContains("dashboard"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginFailed() {
        if (driver.getCurrentUrl().contains("dashboard")) {
            return false;
        }

        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE),
                    ExpectedConditions.urlContains("auth/login")
            ));
        } catch (Exception e) {
            return !driver.getCurrentUrl().contains("dashboard");
        }

        return driver.getCurrentUrl().contains("auth/login");
    }

    public String getErrorMessage() {
        try {
            WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
            return error.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
