package com.zshs.rpcframeworksimple.proxy;

import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.socket.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy implements InvocationHandler {

    private final String interfaceName;

    public RpcProxy(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取接口的全限定名
        String interfaceName = method.getDeclaringClass().getName();
        // 获取方法名
        String methodName = method.getName();
        // 获取参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 获取参数列表
        Object[] parameters = args;
        // 封装请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(interfaceName)
                .methodName(methodName)
                .parameters(parameters)
                .paramTypes(parameterTypes)
                .build();

        RpcClient rpcClient = new RpcClient();
        RpcResponse<String> result = (RpcResponse<String>) rpcClient.send(rpcRequest, "127.0.0.1", 6666);

        return result.getData();
    }

    public static <T> T createProxy(Class<T> interfaceClass, Class<?> implClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy(implClass.getName())
        );
    }
}
