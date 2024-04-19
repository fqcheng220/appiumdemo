package com.fqcheng220.autotest.appiumdemo.dfcf;

import com.fqcheng220.autotest.appiumdemo.AutoTest;
import com.fqcheng220.autotest.appiumdemo.dfcf.analysis.MultiLogAnalysiser;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 同花顺 app操作
 * app版本 10.80.01
 */
public class AutoTestAndroidNativeDfcf implements AutoTest {
    String PATH_APK_THS = "D:\\MyWork\\Java\\Projects\\appiumdemo\\src\\main\\resources\\portal-eastmoneyGuba-debug.apk";

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
                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/tv_positive")).click();
                sleep(5 * 1000);
                //点击“跳过”
                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/skip_operate")).click();
                sleep(5 * 1000);

                //点击设备权限获取提醒“我知道了”
                driver.findElement(By.xpath("//*[@text='我知道了']")).click();
                sleep(5 * 1000);

                //电话权限获取
                driver.findElement(By.xpath("//*[@text='始终允许']")).click();
                sleep(5 * 1000);
                /**
                 * 点击底部“交易”tab
                 */
                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/btn6")).click();
                sleep(10 * 1000);


//                //点击买入
//                driver.findElement(By.xpath("//*[@text='买入']")).click();
                //点击卖出
                driver.findElement(By.xpath("//*[@text='卖出']")).click();
                sleep(5 * 1000);
                //登录页面
                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/account_tv")).sendKeys("310100020239");
                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/password_tv")).sendKeys("517517");
                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/check_box")).click();
                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/trade_login_btn")).click();
                sleep(5 * 1000);

                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/phone_et")).sendKeys("11111111111");
                driver.findElement(By.id("com.eastmoney.android.gubaproj:id/sms_vercode_et")).sendKeys("111111");
                for (int i = 0; i < 4; i++) {
                    driver.findElement(By.id("com.eastmoney.android.gubaproj:id/validate_btn")).click();
                    sleep(1 * 1000);
                }
                sleep(10 * 1000);
                final AndroidDriver fDriver = driver;
                new MultiLogAnalysiser().analysisLog(new Runnable() {
                    public void run() {
                        //输入股票代码
                        fDriver.findElement(By.id("com.eastmoney.android.gubaproj:id/edit_ctrl")).sendKeys("600519");
                        sleep(5 * 1000);
                        fDriver.findElement(By.id("com.eastmoney.android.gubaproj:id/buy_sell_price")).sendKeys("0.1");
                        fDriver.findElement(By.id("com.eastmoney.android.gubaproj:id/buy_sell_amount_and_total")).sendKeys("100");
                        //点击买入按钮
                        fDriver.findElement(By.id("com.eastmoney.android.gubaproj:id/button_buy_sell")).click();
                        sleep(1 * 1000);
//                        //点击确认买入
//                        fDriver.findElement(By.xpath("//*[@text='确认买入']")).click();
                        //点击确认卖出
                        fDriver.findElement(By.xpath("//*[@text='确认卖出']")).click();
                        sleep(5 * 1000);
                        fDriver.findElement(By.xpath("//*[@text='确定']")).click();
                    }
                });
                while (true) ;
            } finally {
                driver.quit();
            }
        }
    }


    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
