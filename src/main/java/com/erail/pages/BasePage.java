package com.erail.pages;

import com.erail.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait")));
        initPageElements();
    }

    protected void initPageElements() {
        PageFactory.initElements(driver, this);
    }

    protected void refreshElements() {
        PageFactory.initElements(driver, this);
    }

    protected void openPage(String url) {
        driver.get(url);
        initPageElements();
    }
}
