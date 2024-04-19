package com.fqcheng220.autotest.appiumdemo;

public class Logger {
    public static void debug(String msg){
        System.out.println(msg);
    }

    public static void error(String msg){
        System.out.println("error---------------------------");
        System.out.println(msg);
    }
}
