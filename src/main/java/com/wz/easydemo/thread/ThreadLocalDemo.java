package com.wz.easydemo.thread;

import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.collect.Lists;
import com.wz.easydemo.utils.ThreadUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalDemo {
    private static void threadLocalTest() {
        ThreadUtils.THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        ThreadUtils.print(ThreadUtils.THREAD_LOCAL);

        // Thread-0 : null
        // 子线程获取不到主线程的线程变量
        ThreadUtils.print(ThreadUtils.THREAD_LOCAL);
    }

    private static void inheritableThreadLocalTest1() {
        ThreadUtils.INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL);

        // Thread-0 : [1, 2]
        // 创建子线程时会自动继承父线程的线程变量，Thread.init
        new Thread(() -> {
            List<Integer> list = ThreadUtils.INHERITABLE_THREAD_LOCAL.get();
            System.out.println(Thread.currentThread().getName() + " : " + list);
            list.add(3);
        }).start();

        ThreadUtils.sleep(1);
        // main : [1, 2, 3]
        // 可以重写 InheritableThreadLocal.childValue 来改变拷贝方式
        ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL);

        new Thread(() -> {
            List<Integer> list = ThreadUtils.INHERITABLE_THREAD_LOCAL.get();
            System.out.println(Thread.currentThread().getName() + " : " + list);
            ThreadUtils.INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(10));
        }).start();

        ThreadUtils.sleep(1);
        // main : [1, 2, 3]
        // 重新赋值不会生效
        ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL);
    }

    private static void inheritableThreadLocalTest2() {
        ThreadUtils.INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL);

        // pool-1-thread-1 : [1, 2]
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL));

        ThreadUtils.sleep(1);

        ThreadUtils.INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2, 3));
        // main : [1, 2, 3]
        ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL);

        // pool-1-thread-1 : [1, 2]
        // 因为线程复用，子线程获取到的还是之前的值
        executor.execute(() -> ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL));

        executor.shutdown();
    }

    private static void transmittableThreadLocalTest1() {
        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);

        // pool-1-thread-1 : [1, 2]
        ExecutorService executor = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());
        executor.execute(() -> ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL));

        ThreadUtils.sleep(1);

        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2, 3));
        // main : [1, 2, 3]
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);

        // pool-1-thread-1 : [1, 2, 3]
        // 子线程能获取到主线程新的新值
        executor.execute(() -> ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL));
    }

    private static void transmittableThreadLocalTest2() {
        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);

        ExecutorService executor = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());

        executor.execute(() -> {
            // 10 不会影响主线程
            ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(10));
            // 20 在主线程中不生效，会在 TransmittableThreadLocal.restoreTtlValues 时被 remove
            ThreadUtils.SUB_TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(20));
        });

        ThreadUtils.sleep(1);

        // 子线程中的变更不会传递到下一次任务中
        // main: [1, 2]
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
        // main: null
        ThreadUtils.print(ThreadUtils.SUB_TRANSMITTABLE_THREAD_LOCAL);
        executor.shutdown();
    }

    private static void transmittableThreadLocalTest3() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> ThreadUtils.SUB_TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(10)));

        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1));
        executor.execute(Objects.requireNonNull(TtlRunnable.get(() -> {
            // pool-1-thread-1 : [1]
            ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
            // pool-1-thread-1 : null
            // replayTtlValues 时 backup 会有 10，但 captured 中不存在，10 会被 remove
            ThreadUtils.print(ThreadUtils.SUB_TRANSMITTABLE_THREAD_LOCAL);
        })));

        executor.shutdown();
    }

    private static void transmittableThreadLocalTest4() {
        ExecutorService executor = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());
        executor.execute(() -> ThreadUtils.SUB_TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(10)));

        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));

        executor.execute(ThreadLocalDemo::func1);
        executor.shutdown();
    }

    private static void func1() {
        ThreadUtils.sleep(10);
        ExecutorService executor = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());
        executor.execute(() -> {
            // pool-2-thread-1 : [1, 2]
            ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
            // pool-2-thread-1 : null
            ThreadUtils.print(ThreadUtils.SUB_TRANSMITTABLE_THREAD_LOCAL);
        });
        executor.shutdown();
    }

    public static void main(String[] args) {
//        threadLocalTest();
//        inheritableThreadLocalTest1();
//        inheritableThreadLocalTest2();
//        transmittableThreadLocalTest1();
//        transmittableThreadLocalTest2();
//        transmittableThreadLocalTest3();
        transmittableThreadLocalTest4();
    }
}
