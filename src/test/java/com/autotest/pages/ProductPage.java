package com.autotest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProductPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public WebElement findAddButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn.btn-primary")));
    }

    public WebElement findNameField() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
    }

    public WebElement findTypeField() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("type")));
    }

    public WebElement findExoticCheckbox() {
        return wait.until(ExpectedConditions.elementToBeClickable(By.name("exotic")));
    }

    public WebElement findSaveButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#save")));
    }

    // Метод для добавления товара
    public void addNewProduct(String name, String type, boolean isExotic) {
        findAddButton().click();
        findNameField().sendKeys(name);
        findTypeField().sendKeys(type);
        if (isExotic) findExoticCheckbox().click();
        findSaveButton().click();
    }
}
