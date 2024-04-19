package com.fqcheng220.autotest.appiumdemo.demo;

public class ArgsMain {
    public static void main(String[] args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                System.out.println("args[" + i + "]=" + args[i]);
            }
        }
    }
}
