package com.fqcheng220.autotest.appiumdemo.demo;

public class HashCodeDemo {
    /**
     * 通过查看{@link String#hashCode()} jdk1.8源码实现，是存在不同字符串对象hashcode相等的情况
     * @param args
     */
    public static void main(String[] args) {
        String s1 = "Ab";
        String s2= "BC";
        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());
        System.out.println("BB".hashCode());
        System.out.println("BC".hashCode());
    }
}
