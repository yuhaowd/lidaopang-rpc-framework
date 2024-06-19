package com.zshs.rpcframeworksimple.proxy.test;

import java.lang.reflect.Proxy;

public class ProxyFactory {

//    public static <T> T create(Class<T> interfaceClass, String host, int port) {
//        return (T) java.lang.reflect.Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new MyInvocationHandler(host, port));
//    }

    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new MyInvocationHandler(target));
    }
}
