package com.mxixm.experiment.fakeclassloader;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * fake the cacheInterceptor
 */
public class RedisConfig extends CachingConfigurerSupport implements InitializingBean {

    @Autowired
    CacheInterceptor cacheInterceptor;

    @Override
    public void afterPropertiesSet() throws Exception {
        Field evaluatorField = ReflectionUtils.findField(cacheInterceptor.getClass(), "evaluator");
        evaluatorField.setAccessible(true);
        CachedExpressionEvaluator evaluator = (CachedExpressionEvaluator) evaluatorField.get(cacheInterceptor);
        Field discovererField = ReflectionUtils.findField(evaluator.getClass(), "parameterNameDiscoverer");
        discovererField.setAccessible(true);
        DefaultParameterNameDiscoverer discoverer = (DefaultParameterNameDiscoverer) discovererField.get(evaluator);
        discoverer.addDiscoverer(new StandardReflectionParameterNameDiscoverer());
    }

}
