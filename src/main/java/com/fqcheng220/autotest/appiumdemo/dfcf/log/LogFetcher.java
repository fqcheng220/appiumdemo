package com.fqcheng220.autotest.appiumdemo.dfcf.log;

import java.io.InputStream;

/**
 * 抓取log进行消费分析
 * 抓取来源可以是adb logcat或者文件等
 */
public interface LogFetcher {

    void fetchLogStart(LogConsumer logConsumer);
    void fetchLogEnd();
    
    interface LogConsumer{
        boolean consumeInputStream(InputStream inputStream,String tag);
    }
}
