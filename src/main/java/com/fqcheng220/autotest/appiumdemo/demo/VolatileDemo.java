package com.fqcheng220.autotest.appiumdemo.demo;

import java.io.PrintStream;

public class VolatileDemo {
    private int mMem = 0;
    public void testVolatile(){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mMem++;
            }
        });
        thread.start();
        while (mMem == 0){
            int i = 0;
            i++;
            /**
             * 如果{@link #mMem}不用volatile关键字修饰，执行了如下语句也会让线程工作内存数据及时更新，如果没有的话则需要{@link #mMem}用volatile关键字修饰
             * 原因：{@link PrintStream#flush()}、{@link PrintStream#println(int)}都涉及到系统调用，可能引起进程调度，触发线程工作内存数据失效，重新从主内存刷新拷贝？？？
             */
            System.out.flush();
        }
    }

    public static void main(String[] args) {
        VolatileDemo demo = new VolatileDemo();
        demo.testVolatile();
    }
}
