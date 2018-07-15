package com.mxixm.experiment.fakeclassloader;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.InvocationTargetException;

/**
 * @author guangshan
 * @since 2018/1/5
 */
@SpringBootApplication
public class Starter {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        DelegateStarter.run(Starter.class);
    }
}
