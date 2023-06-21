package com.fqcheng220.autotest.appiumdemo;

public class Main {

    public static void main(String[] args) {
//        AutoTest autoTest = new AutoTestAndroidNative();
        AutoTest autoTest = new AutoTestAndroidNativeTonghuashun();
//        AutoTest autoTest = new AutoTestAndroidWebViewDemo();
//        AutoTest autoTest = new AutoTestAndroidWebViewQonline();
        autoTest.start();
//        nativeTest2();
    }

//    private static void nativeTest2() {
//        UiAutomator2Options options = new UiAutomator2Options()
//                .setUdid("8DF6R16826005374")
//                .setApp("D:\\MyWork\\Java\\Projects\\appiumdemo\\src\\main\\resources\\dfcft108_publish_6ce842_51_arm64-v8a_encrypt.apk");
//        AndroidDriver driver = null;
//        try {
//            driver = new AndroidDriver(
//                    // The default URL in Appium 1 is http://127.0.0.1:4723/wd/hub
//                    new URL("http://127.0.0.1:4723"), options
//            );
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        if (driver != null) {
//            try {
////                WebElement el = driver.findElement(AppiumBy.xpath, "//Button");
//                WebElement el = driver.findElement(By.className("android.webkit.WebView"));
////                el.click();
//                driver.getPageSource();
//            } finally {
//                driver.quit();
//            }
//        }
//    }
}
