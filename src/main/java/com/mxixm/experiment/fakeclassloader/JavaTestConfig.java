package com.mxixm.experiment.fakeclassloader;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Callable;

@Configuration
public class JavaTestConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(test("aaa"));
    }

    private String test(String arg) throws Exception {
        Auxiliary auxiliary = new Auxiliary();
        auxiliary.target = this;
        AuxiliaryCall auxiliaryCall = new AuxiliaryCall(this, arg);
        return (String) auxiliaryCall.call();
    }

    private String test2(String arg) {
        System.out.println(arg);
        return arg + 1;
    }

    class Auxiliary {
        JavaTestConfig target;
    }

    class AuxiliaryCall implements Callable {

        JavaTestConfig javaTestConfig;

        String str;

        AuxiliaryCall(JavaTestConfig javaTestConfig, String str) {
            this.javaTestConfig = javaTestConfig;
            this.str = str;
        }

        @Override
        public Object call() throws Exception {
            return javaTestConfig.test2(str);
        }
    }

}
