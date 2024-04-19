package com.fqcheng220.autotest.appiumdemo.dfcf.log;

import com.fqcheng220.autotest.appiumdemo.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * adb logcat抓取log消费分析
 */
public class AdbLogcatLogFetcher implements LogFetcher {
    private Process mProcess = null;

    public void fetchLogStart(LogConsumer logConsumer) {
        clearLog();
        mProcess = captureLog(logConsumer);
    }

    public void fetchLogEnd() {
        waitProcess(mProcess, "adb logcat");
    }

    private void clearLog() {
        String[] cmds = new String[]{"adb", "logcat", "-c"};
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmds);
            waitProcess(process, "adb locat -c");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Process captureLog(final LogConsumer logConsumer) {
        String[] cmds = new String[]{"adb", "logcat", "|", "findstr", "EM-Trade"};
        try {
            final Process process = Runtime.getRuntime().exec(cmds);

            final InputStream inputStream = process.getInputStream();
            final InputStream errorStream = process.getErrorStream();
            Thread threadInput = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (logConsumer != null && logConsumer.consumeInputStream(inputStream, "inputStream")) {
                        process.destroy();
                    }
                }
            });
            threadInput.start();
            Thread threadError = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (logConsumer != null && logConsumer.consumeInputStream(errorStream, "errorStream")) {
                        process.destroy();
                    }
                }
            });
            threadError.start();
            return process;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void waitProcess(Process process, String tag) {
        if (process == null)
            return;
        if (tag == null)
            tag = "";
        long curTime = System.currentTimeMillis();
        Logger.debug(String.format("process.waitFor() %s %d start", tag, Thread.currentThread().getId()));
        try {
            boolean processTimeout = !process.waitFor(3600 * 1000, TimeUnit.MILLISECONDS);
            Logger.debug(String.format("process.waitFor()  %s  %d end,costTime=%d", tag, Thread.currentThread().getId(), (System.currentTimeMillis() - curTime)));
            if (processTimeout) {
                Logger.error(String.format("skip %s due to processTimeout", tag));
                throw new RuntimeException(String.format("skip %s due to processTimeout", tag));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
