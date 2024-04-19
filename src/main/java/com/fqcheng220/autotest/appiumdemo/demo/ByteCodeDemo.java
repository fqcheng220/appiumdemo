package com.fqcheng220.autotest.appiumdemo.demo;

import java.net.URI;

/**
 * hotsopt jvm字节码demo
 */
public class ByteCodeDemo {
    private String mStr;

    public void testFieldWrite() {
        mStr = "123";
        mStr = "456";
    }

    /**
     * 不使用局部变量进行多次成员变量的读操作
     * public void testFieldRead();
     *     Code:
     *        0: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
     *        3: aload_0
     *        4: getfield      #3                  // Field mStr:Ljava/lang/String;
     *        7: invokevirtual #6                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     *       10: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
     *       13: aload_0
     *       14: getfield      #3                  // Field mStr:Ljava/lang/String;
     *       17: invokevirtual #6                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     *       20: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
     *       23: aload_0
     *       24: getfield      #3                  // Field mStr:Ljava/lang/String;
     *       27: invokevirtual #6                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     *       30: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
     *       33: aload_0
     *       34: getfield      #3                  // Field mStr:Ljava/lang/String;
     *       37: invokevirtual #6                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     *       40: return
     */
    public void testFieldRead() {
        System.out.println(mStr);
        System.out.println(mStr);
        System.out.println(mStr);
        System.out.println(mStr);
    }

    /**
     * 使用局部变量优化多次成员变量的读操作
     * public void testFieldRead2();
     *     Code:
     *        0: aload_0
     *        1: getfield      #3                  // Field mStr:Ljava/lang/String;
     *        4: astore_1
     *        5: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
     *        8: aload_1
     *        9: invokevirtual #6                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     *       12: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
     *       15: aload_1
     *       16: invokevirtual #6                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     *       19: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
     *       22: aload_1
     *       23: invokevirtual #6                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     *       26: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
     *       29: aload_1
     *       30: invokevirtual #6                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     *       33: return
     *
     *
     *  相比较{@link #testFieldRead()}，因为使用了局部变量（只新增了一个局部变量空间），所以每次调用方法System.out.println传参（注意：第一个参数是this指针，这里说的是第二个参数）
     *  时由原来的两条字节码指令换成了一条字节码指令
     *        3: aload_0
     *        4: getfield      #3                  // Field mStr:Ljava/lang/String;
     *  变成了
     *        8: aload_1
     *  如果函数体内有多次读成员变量操作的话，这种写法优势就体现出来了
     */
    public void testFieldRead2() {
        final String str = mStr;
        System.out.println(str);
        System.out.println(str);
        System.out.println(str);
        System.out.println(str);
    }

    public static void main(String[] args) {
        ByteCodeDemo byteCodeDemo = new ByteCodeDemo();
        byteCodeDemo.testFieldWrite();
    }
}