package com.fqcheng220.autotest.appiumdemo;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;

public class AutoTestAndroidNative implements AutoTest{
    public void start() {
        UiAutomator2Options options = new UiAutomator2Options()
//                .setUdid("8DF6R16826005374")
                .setApp(PATH_APK_WEBVIEW_DEMO);
        AndroidDriver driver = null;
        try {
            driver = new AndroidDriver(
                    // The default URL in Appium 1 is http://127.0.0.1:4723/wd/hub
                    new URL("http://127.0.0.1:4723"), options
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (driver != null) {
            try {
//                WebElement el = driver.findElement(AppiumBy.xpath, "//Button");
                WebElement el = driver.findElement(By.className("android.webkit.WebView"));
//                el.click();
                driver.getPageSource();
                while (true);
            } finally {
                driver.quit();
            }
        }
    }
}
