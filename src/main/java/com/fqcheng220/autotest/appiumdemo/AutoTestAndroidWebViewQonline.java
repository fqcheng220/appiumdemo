package com.fqcheng220.autotest.appiumdemo;

import io.appium.java_client.AppiumFluentWait;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class AutoTestAndroidWebViewQonline implements AutoTest {
    private static final String PATH_CHROME_DRIVER_WIN_2_28 = "D:\\MyWork\\Java\\Projects\\appiumdemo\\src\\main\\resources\\windows\\2.28\\chromedriver_win32\\chromedriver.exe";
    private static final String PATH_CHROME_DRIVER_WIN_81_0 = "D:\\MyWork\\Java\\Projects\\appiumdemo\\src\\main\\resources\\windows\\81.0.4044.138\\chromedriver_win32\\chromedriver.exe";
    private static final String PATH_CHROME_DRIVER_WIN_83_0 = "D:\\MyWork\\Java\\Projects\\appiumdemo\\src\\main\\resources\\windows\\83.0.4103.14\\chromedriver_win32\\chromedriver.exe";
    private static final String PATH_CHROME_DRIVER_WIN_89_0 = "D:\\MyWork\\Java\\Projects\\appiumdemo\\src\\main\\resources\\windows\\89.0.4389.23\\chromedriver_win32\\chromedriver.exe";
    private static final String PATH_CHROME_DRIVER_WIN_90_0 = "D:\\MyWork\\Java\\Projects\\appiumdemo\\src\\main\\resources\\windows\\90.0.4430.24\\chromedriver_win32\\chromedriver.exe";

    @Override
    public void start() {
//        UiAutomator2Options options = new UiAutomator2Options()
////                .setUdid("8DF6R16826005374")
//                .setApp(PATH_APK_WEBVIEW_DEMO)
//                .setChromedriverExecutable(PATH_CHROME_DRIVER_WIN_89_0);

        DesiredCapabilities options = new DesiredCapabilities();
        options.setCapability("automationName", "UIAutomator2");
        options.setCapability("platformName", "Android");
//        options.setCapability("browserName", "Chrome");
        options.setCapability("app", PATH_APK_WEBVIEW_DEMO);
//                .setApp(PATH_APK_WEBVIEW_DEMO)
        options.setCapability("appium:intentAction", "android.intent.action.VIEW");
        options.setCapability("appium:optionalIntentArguments", "-d http://www.qonline.cc");
        options.setCapability("chromedriverExecutable", PATH_CHROME_DRIVER_WIN_2_28);

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

                WebElement el = driver.findElement(By.className("android.webkit.WebView"));
                System.out.println("" + driver.getContext());
                for (String ctx : driver.getContextHandles()) {
                    System.out.println("-------" + ctx);
                }

                driver.context("WEBVIEW_com.fqcheng220.android.selendroidwebdemoapk");

                step1ClickToLogin(driver);
                step2InputLogin(driver);
                step3SwitchTabRecord(driver);
                while (true) ;
            } finally {
                driver.quit();
            }
        }
    }

    private void step1ClickToLogin(RemoteWebDriver driver) {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement btnElement = driver.findElement(By.cssSelector(".mybtn1"));
        /**
         * <div data-v-376ed18a="" class="mybtn1">
         *        Get Started
         *       </div>
         *  div块无法直接调用click方法点击,改成使用js脚本
         */
//                btnElement.click();
        driver.executeScript("javascript:var element = document" + ".getElementsByClassName" +
                "(\"mybtn1\")[0]; const eventHaha = new " + "MouseEvent" + "(\"tap\", " +
                "{view:window, bubbles:true, cancelable:true}); var canceled " + "= " + "element" +
                ".dispatchEvent(eventHaha);");
    }

    private void step2InputLogin(RemoteWebDriver driver) {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String userName = "xxx";//需要替换成真实用户名
        final String userPwd = "xxx";//需要替换成真实密码
        driver.executeScript(String.format("javascript:" +
                "document.getElementsByClassName(\"user_input\")[0].children[1].value='%s';" +
                "document.getElementsByClassName(\"psd_input\")[0].children[1].value='%s';" +

                "document.getElementsByClassName(\"user_input\")[0].children[1].dispatchEvent(new CustomEvent('change'));" +
                "document.getElementsByClassName(\"user_input\")[0].children[1].dispatchEvent(new CustomEvent('input'));" +

                "document.getElementsByClassName(\"psd_input\")[0].children[1].dispatchEvent(new CustomEvent('change'));" +
                "document.getElementsByClassName(\"psd_input\")[0].children[1].dispatchEvent(new CustomEvent('input'));" +

                "var element = document.getElementsByClassName(\"btn\")[1]; " +
                "const event2 = new " + "MouseEvent" + "(\"tap\", {view:window, bubbles:true, cancelable:true}); " +
                "var canceled = element.dispatchEvent(event2);",userName,userPwd));
    }

    private void step3SwitchTabRecord(RemoteWebDriver driver) {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.executeScript("javascript:function sleep (time) {\n" +
                "  return new Promise((resolve) => setTimeout(resolve, time));\n" +
                "}\n" +
                "\n" +
                "function _switchTabRecord() {\n" +
                "// alert(document.querySelectorAll('#app > div > div:nth-child(1) > nav')[0].children[3].innerHTML);\n" +
                "// alert(document.querySelectorAll('#app > div > div:nth-child(1) > nav > a:nth-child(4)')[0].innerHTML);\n" +
                "\n" +
                "  // 查找Record tab按钮\n" +
                "  // var recordElement = document.querySelectorAll('#app > div > div:nth-child(1) > nav')[0].children[3]; \n" +
                "    var recordElement = document.querySelectorAll('#app > div > div:nth-child(1) > nav > a:nth-child(4)')[0]; \n" +
                "  // // 模拟点击 错误\n" +
                "  // recordElement.click();\n" +
                "  \n" +
                "    // 创建点击事件\n" +
                "  const recordEvent = new MouseEvent(\"tap\", {view:window, bubbles:true, \n" +
                "  cancelable:true}); \n" +
                "  // 模拟点击\n" +
                "  var canceled = recordElement.dispatchEvent(recordEvent); \n" +
                "  \n" +
                "  // showTip(\"Hello\",recordElement.innerHTML,\"Hello\");\n" +
                "}\n" +
                "\n" +
                "// swal2-confirm swal2-styled\n" +
                "\n" +
                "_switchTabRecord();");
    }
}
