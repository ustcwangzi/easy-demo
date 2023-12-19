package com.wz.easydemo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class EasyDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyDemoApplication.class, args);
        Scanner input = new Scanner(System.in);
        String val;

        do {
            System.out.print("请输入时间范围，以-分割(如201601-202012)，输入exit退出:");
            val = input.next();
            if ("exit".equalsIgnoreCase(val)) {
                System.out.println("程序结束");
            } else if (!check(val)) {
                System.out.println("输入错误");
            } else {
                String[] array = val.trim().split("-");
                System.out.println("开始时间:" + Integer.parseInt(array[0]) + ", 结束时间:" + Integer.parseInt(array[1]));
            }
        } while (!"exit".equalsIgnoreCase(val));
        input.close();
    }

    private static boolean check(String value) {
        if (StringUtils.isEmpty(value) || !value.contains("-")) {
            return false;
        }
        String[] array = value.trim().split("-");
        if (array.length != 2) {
            return false;
        }
        for (String cur : array) {
            if (!StringUtils.isNumeric(cur)) {
                return false;
            }
            int time = Integer.parseInt(cur);
            if (time > 202012 || time < 201601) {
                return false;
            }
        }
        return true;
    }
}
