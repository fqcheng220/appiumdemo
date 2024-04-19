package com.fqcheng220.autotest.appiumdemo.demo;

public class SyncClass {

    private static String sKey = "123";

    public static void main(String[] args) {
        testStringCpy();
    }

    /**
     * String类型赋值操作
     */
    private static void testStringCpy(){
        String s1,s2;
        s1 = "123";
        s2 = s1;
        System.out.println(String.format("s1=%s,s2=%s",s1,s2));
        s1 = "456";
        System.out.println(String.format("s1=%s,s2=%s",s1,s2));

        System.out.println(1 & -2);
        System.out.println(2 & -3);
        System.out.println(3 & -4);
        System.out.println(4 & -5);
        System.out.println(5 & -6);
        System.out.println(6 & -7);
        System.out.println(7 & -8);
    }

    private static void testVolatile(){
        for (int i = 0; i < 10; i++) {
//            System.out.println("test start " + i);
            sKey = "123";
            Thread thread1 = new Thread(new Runnable() {
                public void run() {
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    sKey = null;
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    if (sKey == null) {
                        System.out.println("sKey==null");
                    } else {
//                        System.out.println("sKey!=null");
//                        System.out.println("sKey=" + sKey);
                        if (sKey == null) {
                            System.out.println("-----------------found sKey=" + sKey);
                        }
                    }
                }
            });
            thread1.start();
            thread2.start();
            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            System.out.println("test end " + i);
        }
    }
}
