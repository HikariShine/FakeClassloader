package com.mxixm.experiment.fakeclassloader;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaConfig implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(test("aaa"));
    }

    private String test(String arg) {
        System.out.println(arg);
        return arg + 1;
    }

}
