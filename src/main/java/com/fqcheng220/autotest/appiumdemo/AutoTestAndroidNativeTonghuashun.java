package com.fqcheng220.autotest.appiumdemo;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 同花顺 app操作
 * app版本 10.80.01
 */
public class AutoTestAndroidNativeTonghuashun implements AutoTest{
    String PATH_APK_THS = "D:\\MyWork\\Java\\Projects\\appiumdemo\\src\\main\\resources\\ths_android_V10_80_01.apk";

    public void start() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setApp(PATH_APK_THS);
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
                //点击隐私协议弹窗“同意”
                driver.findElement(By.id("com.hexin.plat.android:id/ok_btn")).click();
                sleep(1*1000);
                //点击“没有炒过股”
                driver.findElement(By.id("com.hexin.plat.android:id/layout_first_level_no_exp")).click();
                sleep(1*1000);
                //点击“随便看看”
                driver.findElement(By.xpath("//*[@text='随便看看']")).click();
                sleep(10*1000);
                /**
                 * 点击底部“交易”tab
                 * 注意xpath字符串的格式，以//开头
                 * 如
                 * driver.findElement(By.id("com.hexin.plat.android:id/tab_widget"));
                 * 等价于
                 * driver.findElement(By.xpath("//android.widget.LinearLayout[@resource-id='com.hexin.plat.android:id/tab_widget']"));
                 */
                driver.findElement(By.xpath("//android.widget.LinearLayout[@resource-id='com.hexin.plat.android:id/tab_widget']/android.widget.RelativeLayout[4]")).click();
                sleep(1*1000);
                try{
                    //点击新手开户福利弹窗右上角关闭按钮,如果有的话，可能也不会弹窗
                    driver.findElement(By.xpath("//*[@ontent-desc='images-19a49913-404502']")).click();
                }catch (Exception e){
                    e.printStackTrace();
                }
                driver.getPageSource();
                while (true);
            } finally {
                driver.quit();
            }
        }
    }

    private void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
