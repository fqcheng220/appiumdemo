package com.fqcheng220.autotest.appiumdemo.dfcf.analysis;

import com.fqcheng220.autotest.appiumdemo.Logger;
import com.fqcheng220.autotest.appiumdemo.dfcf.analysis.LogAnalysiser.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiLogAnalysiser {
    Map<String, SameSessionList> mMapSessions = new HashMap<String, SameSessionList>();
    static final int MAX_LOOP_COUNT = 2;

    public void analysisLog(Runnable runnable) {
        for (int i = 0; i < MAX_LOOP_COUNT; i++) {
            Logger.debug("analysisLog start " + i);
            new LogAnalysiser().analysisLog(prepareSessions(), runnable);
            Logger.debug("analysisLog end " + i);
        }
        cacMapSessions();
        printMapSessions();
    }

    private void printMapSessions() {
        Logger.debug("-------------------------printMapSessions start-------------------------");
        if (mMapSessions != null) {
            for (String key : mMapSessions.keySet()) {
                SameSessionList sameSessionList = mMapSessions.get(key);
                if (sameSessionList != null) {
                    sameSessionList.print();
                }
            }
        }
        Logger.debug("-------------------------printMapSessions end-------------------------");
    }

    private List<Session> prepareSessions() {
        List<Session> sessions = new ArrayList<Session>();

        Session session = new Session(true);
        session.inteName = "tradeStkNativeV1";
        List<String> startTagList = new ArrayList<String>();
        startTagList.add("[FinanceSocketRequestJob]request sent! [[finance][tradeStkNativeV1]");
        List<String> endTagList = new ArrayList<String>();
        endTagList.add("package received!");
        endTagList.add("tradeSt");
        List<String> uiNotifyList = new ArrayList<String>();
        uiNotifyList.add("FP_tradeStkNativeV1 ");
        session.startTagList = startTagList;
        session.endTagList = endTagList;
        session.uiNotifyList = uiNotifyList;
        sessions.add(session);
        updateMapSessions(session);

        session = new Session(true);
        session.inteName = "queryStkAndQuantity";
        startTagList = new ArrayList<String>();
        startTagList.add("[FinanceSocketRequestJob]request sent! [[finance][queryStkAndQuantity]");
        endTagList = new ArrayList<String>();
        endTagList.add("package received!");
        endTagList.add("querySt");
        uiNotifyList = new ArrayList<String>();
        uiNotifyList.add("FP_queryStkAndQuantity ");
        session.startTagList = startTagList;
        session.endTagList = endTagList;
        session.uiNotifyList = uiNotifyList;
        sessions.add(session);
        updateMapSessions(session);

        session = new Session(true);
        session.inteName = "queryCostPrice";
        startTagList = new ArrayList<String>();
        startTagList.add("[FinanceSocketRequestJob]request sent! [[finance][queryCostPrice]");
        endTagList = new ArrayList<String>();
        endTagList.add("package received!");
        endTagList.add("queryCo");
        uiNotifyList = new ArrayList<String>();
        uiNotifyList.add("FP_queryCostPrice ");
        session.startTagList = startTagList;
        session.endTagList = endTagList;
        session.uiNotifyList = uiNotifyList;
        sessions.add(session);
        updateMapSessions(session);

        return sessions;
    }

    private void cacMapSessions() {
        if (mMapSessions != null) {
            for (String key : mMapSessions.keySet()) {
                SameSessionList sameSessionListTmp = mMapSessions.get(key);
                if (sameSessionListTmp != null) {
                    sameSessionListTmp.cac();
                }
            }
        }
    }

    private void updateMapSessions(Session session) {
        if (mMapSessions != null && session != null) {
            SameSessionList sameSessionListTmp;
            if (mMapSessions.containsKey(session.inteName)) {
                sameSessionListTmp = mMapSessions.get(session.inteName);
            } else {
                sameSessionListTmp = new SameSessionList();
                sameSessionListTmp.inteName = session.inteName;
                mMapSessions.put(session.inteName, sameSessionListTmp);
            }
            sameSessionListTmp.add(session);
        }
    }

    static class SameSessionList {
        List<Session> sessions;
        String inteName;
        long averageCostTimeInterface;
        long averageCostTimeUiNotify = 0;

        protected void add(Session session) {
            if (sessions == null) {
                sessions = new ArrayList<Session>();
            }
            sessions.add(session);
        }

        protected void cac() {
            if (sessions != null && !sessions.isEmpty()) {
                long sumInterface = 0;
                long sumUiNotify = 0;
                for (Session session : sessions) {
                    sumInterface += session.costTimeInterface;
                    sumUiNotify += session.costTimeUiNotify;
                }
                averageCostTimeInterface = sumInterface / sessions.size();
                averageCostTimeUiNotify = sumUiNotify / sessions.size();
            }
        }

        protected void print() {
            Logger.debug(String.format("----------%s(averageCostTimeInterface=%d,averageCostTimeUiNotify=%d)----------", inteName, averageCostTimeInterface, averageCostTimeUiNotify));
        }
    }

}
