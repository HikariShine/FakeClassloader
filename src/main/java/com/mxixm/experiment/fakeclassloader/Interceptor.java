package com.mxixm.experiment.fakeclassloader;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class Interceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @This Object o,
                                   @Super Object o2,
                                   @SuperCall Callable c,
                                   @AllArguments Object[] args) throws Exception {
        long start = System.currentTimeMillis();
        try {
            method.setAccessible(true);
            return null;
        } finally {
            System.out.println(method + " took " + (System.currentTimeMillis() - start));
        }
    }
}
