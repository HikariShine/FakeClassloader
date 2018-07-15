package com.mxixm.experiment.fakeclassloader;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import sun.net.www.ParseUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DelegateStarter {

    /**
     * 使用入口代理，用自定义的类加载器加载真实入口类，同时替换线程的上下文类加载器，那么理论上只要不显式声明
     * 使用特定的类加载器，后续的所有类加载都会使用我们自己定义的代理类加载器
     *
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void run(Class<?> clazz) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String var1 = System.getProperty("java.class.path");
        ClassLoader newClassLoader = new DelegateClassLoader(pathToURLs(getClassPath(var1)));
        Thread.currentThread().setContextClassLoader(newClassLoader);
        Class newSourceClass = newClassLoader.loadClass(clazz.getName());
        Class springApplicationClass = newClassLoader.loadClass("org.springframework.boot.SpringApplication.SpringApplication");
        Method springBootRunMethod = springApplicationClass.getMethod("run", Object.class, String[].class);
        springBootRunMethod.invoke(null, new Object[]{newSourceClass, new String[]{}});
    }

    public static class DelegateClassLoader extends URLClassLoader {

        public DelegateClassLoader(URL[] urls) {
            super(urls, null);
        }

        private Map<String, Class> loaded = new ConcurrentHashMap<>();

        TypePool typePool = TypePool.Default.of(this);

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            System.out.println(name);
            Class clazz = loaded.get(name);
            if (clazz != null) {
                return clazz;
            }
            if (name.startsWith("com.mxixm.experiment.fakeclassloader.JavaConfig") && !name.contains("$$")) {
                try {
                    DynamicType.Unloaded<Object> make = new ByteBuddy()
                            .rebase(typePool.describe(name).resolve(),
                                    ClassFileLocator.ForClassLoader.of(this))
                            .defineField("qux", String.class)
                            .method(ElementMatchers.named("test"))
                            .intercept(MethodDelegation.to(Interceptor.class))
                            .make();
                    make.saveIn(new File("D:/class/"));
                    clazz = make.load(this).getLoaded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                clazz = super.findClass(name);
            }
            if (clazz == null) {
                clazz = ClassLoader.getSystemClassLoader().loadClass(name);
            }
            loaded.put(name, clazz);
            return clazz;
        }
    }

    private static File[] getClassPath(String var0) {
        File[] var1;
        if (var0 != null) {
            int var2 = 0;
            int var3 = 1;
            boolean var4 = false;

            int var5;
            int var7;
            for (var5 = 0; (var7 = var0.indexOf(File.pathSeparator, var5)) != -1; var5 = var7 + 1) {
                ++var3;
            }

            var1 = new File[var3];
            var4 = false;

            for (var5 = 0; (var7 = var0.indexOf(File.pathSeparator, var5)) != -1; var5 = var7 + 1) {
                if (var7 - var5 > 0) {
                    var1[var2++] = new File(var0.substring(var5, var7));
                } else {
                    var1[var2++] = new File(".");
                }
            }

            if (var5 < var0.length()) {
                var1[var2++] = new File(var0.substring(var5));
            } else {
                var1[var2++] = new File(".");
            }

            if (var2 != var3) {
                File[] var6 = new File[var2];
                System.arraycopy(var1, 0, var6, 0, var2);
                var1 = var6;
            }
        } else {
            var1 = new File[0];
        }

        return var1;
    }

    private static URL[] pathToURLs(File[] var0) {
        URL[] var1 = new URL[var0.length];

        for (int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = getFileURL(var0[var2]);
        }

        return var1;
    }

    static URL getFileURL(File var0) {
        try {
            var0 = var0.getCanonicalFile();
        } catch (IOException var3) {
            ;
        }

        try {
            return ParseUtil.fileToEncodedURL(var0);
        } catch (MalformedURLException var2) {
            throw new InternalError(var2);
        }
    }

}
