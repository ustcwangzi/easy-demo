package com.wz.easydemo.thread;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.collect.Lists;
import com.wz.easydemo.utils.ThreadUtils;

import java.util.List;
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
        // 创建子线程时回自动继承父线程的线程变量，Thread.init
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
            ThreadUtils.INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(1));
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
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(
                () -> ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL)
        );

        ThreadUtils.sleep(1);

        ThreadUtils.INHERITABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2, 3));
        // main : [1, 2, 3]
        ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL);

        // pool-1-thread-1 : [1, 2]
        // 因为线程复用，子线程获取到的还是之前的值
        executorService.execute(
                () -> ThreadUtils.print(ThreadUtils.INHERITABLE_THREAD_LOCAL)
        );

        executorService.shutdown();
    }

    private static void transmittableThreadLocalTest1() {
        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        // main : [1, 2]
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);

        // pool-1-thread-1 : [1, 2]
        ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());
        executorService.execute(
                () -> ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL)
        );

        ThreadUtils.sleep(1);

        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2, 3));
        // main : [1, 2, 3]
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);

        // pool-1-thread-1 : [1, 2, 3]
        executorService.execute(() -> {
            ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
            ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1));
        });

        ThreadUtils.sleep(1);

        // main: [1, 2, 3]
        // 子线程中的变更不会传递到下一次任务中，注意这里也是浅拷贝
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
        executorService.shutdown();
    }

    private static void transmittableThreadLocalTest2() {
        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2));
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
        ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());
        executorService.execute(ThreadLocalDemo::func1);

        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1, 2, 3));
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
        executorService.shutdown();
    }

    private static void func1() {
        ThreadUtils.sleep(10);
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
//        ThreadUtils.TRANSMITTABLE_THREAD_LOCAL.set(Lists.newArrayList(1,2,3));
        ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());
        executorService.execute(ThreadLocalDemo::func2);
        executorService.shutdown();
    }

    private static void func2() {
        ThreadUtils.sleep(100);
        ThreadUtils.print(ThreadUtils.TRANSMITTABLE_THREAD_LOCAL);
    }

    public static void main(String[] args) {
//        threadLocalTest();
//        inheritableThreadLocalTest1();
//        inheritableThreadLocalTest2();
//        transmittableThreadLocalTest1();
        transmittableThreadLocalTest2();
    }
}
