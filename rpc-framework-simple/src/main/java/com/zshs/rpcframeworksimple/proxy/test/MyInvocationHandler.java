package com.zshs.rpcframeworksimple.proxy.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {


    private final Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("before invoke" + method.getName());
        Object result = method.invoke(target, args);
        if (result instanceof String) {
            result = "hello " + result;
            System.out.println(result);
        }
        System.out.println("after invoke" + method.getName());
        return result;
    }
}