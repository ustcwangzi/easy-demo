package com.wz.easydemo.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadLocalDemo {
    private static final ThreadLocal<List<Integer>> THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<List<Integer>> INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();
    private static final ThreadLocal<List<Integer>> TRANSMITTABLE_THREAD_LOCAL = new TransmittableThreadLocal<>();

    private static void threadLocalTest() {
        THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        System.out.println(Thread.currentThread().getName() + " : " + THREAD_LOCAL.get());

        // Thread-0 : null
        // 子线程获取不到主线程的线程变量
        new Thread(() -> System.out.println(Thread.currentThread().getName() + " : " + THREAD_LOCAL.get())).start();
    }

    private static void inheritableThreadLocalTest1() throws Exception {
        INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        System.out.println(Thread.currentThread().getName() + " : " + INHERITABLE_THREAD_LOCAL.get());

        // Thread-0 : [1, 2]
        new Thread(() -> {
            List<Integer> list = INHERITABLE_THREAD_LOCAL.get();
            System.out.println(Thread.currentThread().getName() + " : " + list);
            list.add(3);
        }).start();

        TimeUnit.MILLISECONDS.sleep(1);
        // main : [1, 2, 3]
        // 可以重写 InheritableThreadLocal.childValue 来改变拷贝方式
        System.out.println(Thread.currentThread().getName() + " : " + INHERITABLE_THREAD_LOCAL.get());
    }

    private static void inheritableThreadLocalTest2() throws Exception {
        INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        System.out.println(Thread.currentThread().getName() + " : " + INHERITABLE_THREAD_LOCAL.get());

        // pool-1-thread-1 : [1, 2]
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(
                () -> System.out.println(Thread.currentThread().getName() + " : " + INHERITABLE_THREAD_LOCAL.get())
        );

        TimeUnit.MILLISECONDS.sleep(1);

        INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2, 3));
        // main : [1, 2, 3]
        System.out.println(Thread.currentThread().getName() + " : " + INHERITABLE_THREAD_LOCAL.get());

        // pool-1-thread-1 : [1, 2]
        // 因为线程复用，子线程获取到的还是之前的值
        executorService.execute(
                () -> System.out.println(Thread.currentThread().getName() + " : " + INHERITABLE_THREAD_LOCAL.get())
        );

        executorService.shutdown();
    }

    private static void transmittableThreadLocalTest() throws Exception {
        TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        System.out.println(Thread.currentThread().getName() + " : " + TRANSMITTABLE_THREAD_LOCAL.get());

        // pool-1-thread-1 : [1, 2]
        ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());
        executorService.execute(
                () -> System.out.println(Thread.currentThread().getName() + " : " + TRANSMITTABLE_THREAD_LOCAL.get())
        );

        TimeUnit.MILLISECONDS.sleep(1);

        TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2, 3));
        // main : [1, 2, 3]
        System.out.println(Thread.currentThread().getName() + " : " + TRANSMITTABLE_THREAD_LOCAL.get());

        // pool-1-thread-1 : [1, 2, 3]
        executorService.execute(
                () -> System.out.println(Thread.currentThread().getName() + " : " + TRANSMITTABLE_THREAD_LOCAL.get())
        );

        executorService.shutdown();
    }

    public static void main(String[] args) throws Exception {
//        threadLocalTest();
//        inheritableThreadLocalTest1();
//        inheritableThreadLocalTest2();
        transmittableThreadLocalTest();
    }
}
