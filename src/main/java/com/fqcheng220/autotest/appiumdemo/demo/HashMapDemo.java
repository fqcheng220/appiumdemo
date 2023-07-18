package com.fqcheng220.autotest.appiumdemo.demo;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 一个待解决问题
 * HashMap并发读写（get和put/remove）会有冲突吗？会崩溃吗？
 */
public class HashMapDemo {
    private Map<String, String> map = new HashMap<String, String>();

    /**
     * 使用{@link Map#remove(Object)}和{@link Iterator#next()}
     * 报错java.util.ConcurrentModificationException
     */
    public void testIteratorInSingleThread() {
        map.put("1", "1");
        map.put("2", "2");
        if (map != null) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                map.remove(key);
            }
        }
    }

    /**
     * 一个线程通过{@link HashMap#keySet()} {@link HashMap.KeySet#iterator()} 获得内部类{@link HashMap.KeyIterator}执行{@link HashMap.KeyIterator#next()}
     * 会判断{@link HashMap#modCount}和{@link java.util.HashMap.KeyIterator#expectedModCount}是否一致，不一致的话会抛出受控异常ConcurrentModificationException
     * final Node<K,V> nextNode() {
     * Node<K,V>[] t;
     * Node<K,V> e = next;
     * if (modCount != expectedModCount)
     * throw new ConcurrentModificationException();
     * if (e == null)
     * throw new NoSuchElementException();
     * if ((next = (current = e).next) == null && (t = table) != null) {
     * do {} while (index < t.length && (next = t[index++]) == null);
     * }
     * return e;
     * }
     * <p>
     * 所以如果此时有另外一个线程操作{@link HashMap}执行类似删除{@link HashMap#remove(Object)}、新增{@link HashMap#put(Object, Object)}，
     * 因为会影响到{@link HashMap#modCount}的变化，就会导致受控异常ConcurrentModificationException抛出
     */
    public void testIterator() {
        for (int i = 0; i < 10; i++) {
            System.out.println("testIterator start " + i);
            map = new HashMap<String, String>();
            map.put("1", "1");
            map.put("2", "2");
            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (map != null) {
                        Iterator<String> iterator = map.keySet().iterator();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            map.get(key);
                        }
                    }
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    if (map != null) {
//                        Iterator<String> iterator = map.keySet().iterator();
//                        while (iterator.hasNext()) {
//                            String key = iterator.next();
//                            map.remove(key);
//                            return;
//                        }
//                    }
                    if (map != null && map.containsKey("1")) {
                        map.remove("1");
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
            System.out.println("testIterator end " + i);
        }
    }

    /**
     * 并发写冲突，hash碰撞（这里举例的是hash相同，所以映射到数组的索引也是相同，实际上不同hash也可能映射到相同的数组索引从而产生同样的碰撞问题）
     * 存在添加元素被覆盖的问题
     * <p>
     * 测试发现另外一个并发问题
     * 并发{@link HashMap#put(Object, Object)}，但是{@link HashMap#keySet()}的size对不上
     * 原因是{@link HashMap#put(Object, Object)}有并发执行代码片段++size导致最后的siez变量读取（调用map.keySet().size()）也是错误的
     * 参考{@link #testConcurrentPutThenReadKeySize()}
     */
    public void testConcurrentPut() {
        for (int i = 0; i < 10; i++) {
//            System.out.println("testConcurrentPut start " + i);
            map = new HashMap<String, String>();
            map.put("3", "3");

            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map.put("Ab", "Ab");
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map.put("BC", "BC");
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
            System.out.println("testConcurrentPut " + i);
            Iterator<String> iterator2 = map.keySet().iterator();
            while (iterator2.hasNext()) {
                String key = iterator2.next();
                System.out.println("testConcurrentPut print " + i + ",key=" + key + ",value=" + map.get(key));
            }
        }
    }

    /**
     * 另外一个并发问题
     * 并发{@link HashMap#put(Object, Object)}，但是{@link HashMap#keySet()}的size对不上
     */
    public void testConcurrentPutThenReadKeySize() {
        for (int i = 0; i < 10; i++) {
            map = new HashMap<String, String>();
            map.put("3", "3");
            try {
                Field field = HashMap.class.getDeclaredField("table");
                field.setAccessible(true);
                Object obj = field.get(map);
//                System.out.println("table length="+Array.getLength(obj));
//                System.out.println("hash(\"3\")&(Array.getLength(obj)-1)="+(hash("3")&(Array.getLength(obj)-1)));
//                System.out.println("hash(\"BB\")&(Array.getLength(obj)-1)="+(hash("BB")&(Array.getLength(obj)-1)));
//                System.out.println("hash(\"BC\")&(Array.getLength(obj)-1)="+(hash("BC")&(Array.getLength(obj)-1)));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    map.put("Ab", "Ab");
                    map.put("BB", "BB");
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map.put("BC", "BC");
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
            System.out.println("testConcurrentPutThenReadKeySize end " + i + ",keySet size=" + map.keySet().size());
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                System.out.println("testConcurrentPutThenReadKeySize print " + i + ",key=" + key + ",value=" + map.get(key));
            }
        }
    }

    /**
     * reference {@link HashMap#hashCode()} jdk1.8
     *
     * @param key
     * @return
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 模拟{@link Map#containsKey(Object)}返回true，但是{@link Map#remove(Object)}返回null，也就是删除失败的问题
     */
    public void testConcurrentContainsAndRemove() {
        for (int i = 0; i < 10; i++) {
            System.out.println("testConcurrentContainsAndRemove start " + i);
            map = new HashMap<String, String>();
            map.put("Ab", "Ab");
            map.put("BC", "BC");
            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (map.containsKey("Ab")) {
                        String rmValue = map.remove("Ab");
                        if(rmValue == null)
                            System.out.println("testConcurrentContainsAndRemove thread1 contains true but remove fail ");
                    }
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (map.containsKey("Ab")) {
                        String rmValue = map.remove("Ab");
                        if(rmValue == null)
                            System.out.println("testConcurrentContainsAndRemove thread2 contains true but remove fail ");
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
            System.out.println("testConcurrentContainsAndRemove end " + i);
        }
    }

    /**
     * {@link Collections#synchronizedMap(Map)}的使用
     */
    /**
     * {@link Collections#synchronizedMap(Map)}进行迭代的时候还是需要调用者手动添加同步代码！！！
     * 不然并发很容易报错java.util.ConcurrentModificationException
     */
    public void testIteratorForSynchronizedMap() {
        for (int i = 0; i < 10; i++) {
            System.out.println("testIteratorForSynchronizedMap start " + i);
            final Map<String, String> originMap = new HashMap<String, String>();
            map = Collections.synchronizedMap(originMap);
//            map = new HashMap<String, String>();
            map.put("Ab", "Ab");
            map.put("BC", "BC");
            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (map.containsKey("Ab")) {
                        map.remove("Ab");
                    }
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /**
                     * 迭代的时候如果使用synchronized同步块，必须对SynchronizedMap对象本身加锁（本例中如果是对originMap加锁是不能解决并发冲突问题的）
                     * 因为{@link java.util.Collections.SynchronizedMap}所有的读写操作都是对自身进行加锁的，所以迭代遍历访问也需要对自身加锁
                     */
                    synchronized (map) {
                        Iterator<String> iterator = map.keySet().iterator();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
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
            System.out.println("testIteratorForSynchronizedMap end " + i);
        }
    }

    /**
     * 解决了{@link #testConcurrentPut()}的问题
     */
    public void testConcurrentPutForSynchronizedMap() {
        for (int i = 0; i < 10; i++) {
//            System.out.println("testConcurrentPut start " + i);
            map = Collections.synchronizedMap(new HashMap<String, String>());
            map.put("3", "3");

            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map.put("Ab", "Ab");
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map.put("BC", "BC");
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
            System.out.println("testConcurrentPutForSynchronizedMap " + i);
            Iterator<String> iterator2 = map.keySet().iterator();
            while (iterator2.hasNext()) {
                String key = iterator2.next();
                System.out.println("testConcurrentPutForSynchronizedMap print " + i + ",key=" + key + ",value=" + map.get(key));
            }
        }
    }

    public void testConcurrentContainsAndRemoveForSynchronizedMap() {
        for (int i = 0; i < 10; i++) {
            System.out.println("testConcurrentContainsAndRemoveForSynchronizedMap start " + i);
            map = Collections.synchronizedMap(new HashMap<String, String>());
            map.put("Ab", "Ab");
            map.put("BC", "BC");
            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (map.containsKey("Ab")) {
                        String rmValue = map.remove("Ab");
                        if(rmValue == null)
                            System.out.println("testConcurrentContainsAndRemoveForSynchronizedMap thread1 contains true but remove fail ");
                    }
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (map.containsKey("Ab")) {
                        String rmValue = map.remove("Ab");
                        if(rmValue == null)
                            System.out.println("testConcurrentContainsAndRemoveForSynchronizedMap thread2 contains true but remove fail ");
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
            System.out.println("testConcurrentContainsAndRemoveForSynchronizedMap end " + i);
        }
    }

    public static void main(String[] args) {
        HashMapDemo hashMapDemo = new HashMapDemo();
//        hashMapDemo.testIteratorInSingleThread();
        hashMapDemo.testIterator();
        hashMapDemo.testConcurrentPut();
        hashMapDemo.testConcurrentPutThenReadKeySize();
        hashMapDemo.testConcurrentContainsAndRemove();

//        hashMapDemo.testIteratorForSynchronizedMap();
//        hashMapDemo.testConcurrentPutForSynchronizedMap();
        hashMapDemo.testConcurrentContainsAndRemoveForSynchronizedMap();
    }
}
