package com.fqcheng220.autotest.appiumdemo.dfcf.analysis;

import com.fqcheng220.autotest.appiumdemo.Logger;
import com.fqcheng220.autotest.appiumdemo.dfcf.log.AdbLogcatLogFetcher;
import com.fqcheng220.autotest.appiumdemo.dfcf.log.LogFetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 从log中查找多个指定网络接口序列的
 * 请求发送日志记录行、响应接收日志记录行、ui更新日志记录行
 * 并对每个网络接口分别统计三段日志记录行的中间耗时
 */
public class LogAnalysiser {
    LogFetcher logFetcher;

    public void analysisLog(final List<Session> sessions, Runnable runnable) {
        init();
        logFetcher.fetchLogStart(new LogFetcher.LogConsumer() {
            public boolean consumeInputStream(InputStream inputStream, String tag) {
                Logger.debug(String.format("consumeInputStream %s(%d),%s", tag, Thread.currentThread().getId(), inputStream));
                if (inputStream != null) {
                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            Session session = null;
                            if ((session = match2(line, sessions)) != null) {
//                        Logger.debug(String.format("consumeInputStream %s(%d),%s", tag, Thread.currentThread().getId(), inputStream));
                                if (isAllSessionCompleted(sessions)) {
                                    printAllSessionCompleted(sessions);
                                    return true;
                                }
                            }
                            sb.append(line + "\n");
                        }
//                Logger.debug(sb.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Logger.debug(String.format("finally close consumeInputStream %s(%d),%s", tag, Thread.currentThread().getId(), inputStream));
                    }
                }
                return false;
            }
        });
        if (runnable != null)
            runnable.run();
        logFetcher.fetchLogEnd();
    }

    private void init(){
        logFetcher = new AdbLogcatLogFetcher();
    }

    private boolean match(String text, Session session) {
        boolean bMatch = false;
        if (text != null && session != null) {
            switch (session.consumeStep) {
                case STEP_NONE:
                    bMatch = match(text, session.startTagList);
                    if (bMatch) {
                        session.matchedStartTag = text;
                        session.consumeStep = Session.ConsumeStep.STEP_START_TAG;
                        session.cacDistIfNecessary();
                        Logger.debug(String.format("match matchedStartTag %s", text));
                    }
                    break;
                case STEP_START_TAG:
                    bMatch = match(text, session.endTagList);
                    if (bMatch) {
                        session.matchedEndTag = text;
                        session.consumeStep = Session.ConsumeStep.STEP_END_TAG;
                        session.cacDistIfNecessary();
                        Logger.debug(String.format("match matchedEndTag %s", text));
                    }
                    break;
                case STEP_END_TAG:
                    if (session.uiNotifyEnable) {
                        bMatch = match(text, session.uiNotifyList);
                        if (bMatch) {
                            session.matchedUiNotify = text;
                            session.consumeStep = Session.ConsumeStep.STEP_UI_NOTIFY;
                            session.cacDistIfNecessary();
                            Logger.debug(String.format("match matchedUiNotify %s", text));
                        }
                    }
                    break;
            }
        }
        return bMatch;
    }

    private boolean match(String text, List<String> filters) {
        if (text != null && filters != null) {
            for (String filter : filters) {
                if (!text.contains(filter)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Session match2(String text, List<Session> sessions) {
        if (text != null && sessions != null) {
            for (Session session : sessions) {
                if (session != null && match(text, session)) {
                    return session;
                }
            }
        }
        return null;
    }

    private boolean isAllSessionCompleted(List<Session> sessions) {
        boolean isAllSessionCompleted = true;
        if (sessions != null) {
            for (Session session : sessions) {
                if (!session.canCac()) {
                    isAllSessionCompleted = false;
                    break;
                }
            }
        }
//        Logger.debug(String.format("isAllSessionCompleted %b",isAllSessionCompleted));
        return isAllSessionCompleted;
    }

    private void printAllSessionCompleted(List<Session> sessions) {
        Logger.debug("printAllSessionCompleted");
        if (sessions != null) {
            for (Session session : sessions) {
                session.print();
            }
        }
    }

    public static class Session {
        String inteName;
        boolean uiNotifyEnable;
        List<String> startTagList;
        List<String> endTagList;
        List<String> uiNotifyList;
        String matchedStartTag;
        String matchedEndTag;
        String matchedUiNotify;
        long costTimeInterface;
        long costTimeUiNotify = 0;
        ConsumeStep consumeStep = ConsumeStep.STEP_NONE;

        enum ConsumeStep {
            STEP_NONE,
            STEP_START_TAG,
            STEP_END_TAG,
            STEP_UI_NOTIFY,
        }

        public Session(boolean uiNotifyEnable) {
            this.uiNotifyEnable = uiNotifyEnable;
        }

        public Session() {
            this(false);
        }

        public boolean canCac() {
            return matchedStartTag != null && matchedEndTag != null && (!uiNotifyEnable || matchedUiNotify != null);
        }

        public void cacDistIfNecessary() {
            try {
                if (canCac()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM HH:mm:ss.SSS");
                    Date dateStart = simpleDateFormat.parse(matchedStartTag);
                    Date dateEnd = simpleDateFormat.parse(matchedEndTag);
                    costTimeInterface = dateEnd.getTime() - dateStart.getTime();
                    if (matchedUiNotify != null) {
                        Date dateUiNotify = simpleDateFormat.parse(matchedUiNotify);
                        costTimeUiNotify = dateUiNotify.getTime() - dateEnd.getTime();
                    }
                    Logger.debug(String.format("%s costTimeInterface=%d,costTimeUiNotify=%d", inteName, costTimeInterface, costTimeUiNotify));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void print() {
            Logger.debug(String.format("----------%s(costTimeInterface=%d,costTimeUiNotify=%d)----------", inteName, costTimeInterface, costTimeUiNotify));
            Logger.debug(String.format("matchedStartTag=%s", matchedStartTag));
            Logger.debug(String.format("matchedEndTag=%s", matchedEndTag));
            Logger.debug(String.format("matchedUiNotify=%s", matchedUiNotify));
        }
    }
}
