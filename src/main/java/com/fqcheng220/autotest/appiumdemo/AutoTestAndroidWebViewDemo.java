package com.fqcheng220.autotest.appiumdemo;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class AutoTestAndroidWebViewDemo implements AutoTest{
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
        /**
         * 以下两句代码可以实现appium server通过adb 启动目标activity的时候定制intent启动参数
         * 比如定制intent action和intent data，只要目标activity实现了解析对应intent action和intent data逻辑即可，通常是根据action和data动态获取weburl去load
         * 而且使用了这种方式后，后续appium脚本操作dom树也是不会有变化了（不应该调用方法oldDemoBaiduOper，因为oldDemoBaiduOper中url是有变化的从而dom树也是不一样的 ，而应该调用方法newDemoBaiduOper）
         *
         * 这里的appium:optionalIntentArguments使用方式获取方法：
         * 1.先通过查询官网 https://github.com/appium/appium-uiautomator2-driver#capabilities（因为本项目已经指定了appium server的automationName使用UIAutomator2）
         * 2.再去查看appium server运行日志获取到（有adb shell am start xxx日志，
         * 类似D:\MyWork\Android\Tools\AndroidSDK\platform-tools\adb.exe -P 5037 -s 8DF6R16826005374 shell am start -W -n com.fqcheng220.android.selendroidwebdemoapk/com.fqcheng220.android.selendroidwebdemoapk.MainActivity -S -a android.intent.action.VIEW -c android.intent.category.LAUNCHER -f 0x10200000 -d http://www.baidu.com）

         * ************************开始************************
         */
        options.setCapability("appium:intentAction", "android.intent.action.VIEW");
        options.setCapability("appium:optionalIntentArguments", "-d http://www.baidu.com");
        /**
         * ************************结束************************
         */
        options.setCapability("chromedriverExecutable",PATH_CHROME_DRIVER_WIN_2_28);

        AndroidDriver driver = null;
        try {
            driver = new AndroidDriver(
                    // The default URL in Appium 1 is http://127.0.0.1:4723/wd/hub
                    new URL("http://127.0.0.1:4723"), options
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        oldDemoBaiduOper(driver);
        newDemoBaiduOper(driver);
    }

    /**
     * @param driver
     */
    private void oldDemoBaiduOper(AndroidDriver driver){
        if (driver != null) {
            try {
                WebElement el = driver.findElement(By.className("android.webkit.WebView"));
                System.out.println("" + driver.getContext());
                for (String ctx : driver.getContextHandles()) {
                    System.out.println("-------" + ctx);
                }
                /**
                 * 非常重要，只有经过这步才能操作webview里的dom元素
                 * 但是存在一个问题：
                 * 就是以这种方式启动的webdemoapk加载的url跟直接启动webdemoapk加载的url是不一致的，前者增加了很多查询参数导致返回的hmtl内容有差异，dom树节点都发生了变化
                 * 原因未知，可能是
                 *
                 * 所以需要在加载新的url下返回的html中查找需要的节点
                 *
                 * 跟selendroid情况类似
                 */
                driver.context("WEBVIEW_com.fqcheng220.android.selendroidwebdemoapk");
                /**
                 * 以下尝试切换webview context的调用方法实现是错误的
                 */
//                driver.switchTo().window("WEBVIEW_com.fqcheng220.android.selendroidwebdemoapk");

                WebElement inputField = driver.findElement(By.id("word"));
                inputField.sendKeys("Appium");
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /**
                 * android webview中加载的html内容如下
                 *  <input type="submit" value="百度一下" class="bn" />
                 *
                 *  起初调用WebElement btnConfirm = driver.findElement(By.className(".bn"));
                 *  始终查找不到dom节点！
                 *
                 *  原因：在调用这个方法之前切换过webview上下文
                 *  而在切换到webview上下文之前使用的native上下文上有做过class查找
                 *  WebElement el = driver.findElement(By.className("android.webkit.WebView"));
                 *
                 *  跟踪源码
                 *  ElementLocation.java
                 *
                 *  函数findElement
                 *  public WebElement findElement(RemoteWebDriver driver, SearchContext context, BiFunction<String, Object, CommandPayload> createPayload, By locator) {
                 *         Require.nonNull("WebDriver", driver);
                 *         Require.nonNull("Context for finding elements", context);
                 *         Require.nonNull("Method for creating remote requests", createPayload);
                 *         Require.nonNull("Locator", locator);
                 *         ElementLocation.ElementFinder mechanism = (ElementLocation.ElementFinder)this.finders.get(locator.getClass());
                 *         if (mechanism != null) {
                 *             return mechanism.findElement(driver, context, createPayload, locator);
                 *         } else {
                 *             WebElement element;
                 *             if (locator instanceof Remotable) {
                 *                 try {
                 *                     element = ElementLocation.ElementFinder.REMOTE.findElement(driver, context, createPayload, locator);
                 *                     this.finders.put(locator.getClass(), ElementLocation.ElementFinder.REMOTE);
                 *                     return element;
                 *                 } catch (NoSuchElementException var8) {
                 *                     this.finders.put(locator.getClass(), ElementLocation.ElementFinder.REMOTE);
                 *                     throw var8;
                 *                 } catch (InvalidArgumentException var9) {
                 *                     ;
                 *                 }
                 *             }
                 *
                 *             try {
                 *                 element = ElementLocation.ElementFinder.CONTEXT.findElement(driver, context, createPayload, locator);
                 *                 this.finders.put(locator.getClass(), ElementLocation.ElementFinder.CONTEXT);
                 *                 return element;
                 *             } catch (NoSuchElementException var7) {
                 *                 this.finders.put(locator.getClass(), ElementLocation.ElementFinder.CONTEXT);
                 *                 throw var7;
                 *             }
                 *         }
                 *     }
                 *
                 *     如果之前缓存过ByClassName类型对应的ElementLocation.ElementFinder实例到finders，则下次如果使用ByClassName查找dom节点的话则直接使用之前缓存的ElementLocation.ElementFinder实例
                 *     也就是REMOTE实例，但是webview中dom树节点是没办法通过REMOTE以ByClassName形式访问的，必须让REMOTE以ByCssSelector形式访问
                 *     因此有两种解决方案：
                 *     方案1：
                 *     切换webview context上下文之前不要用native context根据ByClassName查找原生控件
                 *
                 *     方案2：
                 *     webview中的dom节点查找使用By.cssSelector(".bn")形式，其中bn是节点的class，bn之前有一个点（非常重要！！！）
                 *     本案例使用的就是方案2
                 */
                WebElement btnConfirm = driver.findElement(By.cssSelector(".bn"));
                btnConfirm.click();
                while (true);
            } finally {
                driver.quit();
            }
        }
    }

    private void newDemoBaiduOper(AndroidDriver driver){
        if (driver != null) {
            try {
                WebElement el = driver.findElement(By.className("android.webkit.WebView"));
                System.out.println("" + driver.getContext());
                for (String ctx : driver.getContextHandles()) {
                    System.out.println("-------" + ctx);
                }

                driver.context("WEBVIEW_com.fqcheng220.android.selendroidwebdemoapk");

                WebElement inputField = driver.findElement(By.id("index-kw"));
                inputField.sendKeys("Appium");
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                WebElement btnConfirm = driver.findElement(By.id("index-bn"));
                btnConfirm.click();
                while (true);
            } finally {
                driver.quit();
            }
        }
    }
}
