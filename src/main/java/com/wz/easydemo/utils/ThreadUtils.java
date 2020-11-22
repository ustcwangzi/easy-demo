package com.wz.easydemo.utils;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
    public static final ThreadLocal<List<Integer>> THREAD_LOCAL = new ThreadLocal<>();
    public static final ThreadLocal<List<Integer>> INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();
    public static final ThreadLocal<List<Integer>> TRANSMITTABLE_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void print(ThreadLocal<?> threadLocal) {
        System.out.println(Thread.currentThread().getName() + " : " + threadLocal.get());
    }
}
