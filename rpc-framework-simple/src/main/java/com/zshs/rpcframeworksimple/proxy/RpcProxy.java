package com.zshs.rpcframeworksimple.proxy;

import com.zshs.rpcframeworksimple.registry.zk.impl.ZkServiceDiscovery;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class RpcProxy implements InvocationHandler {


    private final String serviceName;
    private final RpcRequestTransport rpcRequestTransport;


    public RpcProxy(String serviceName, RpcRequestTransport rpcRequestTransport) {
        this.serviceName = serviceName;
        this.rpcRequestTransport = rpcRequestTransport;
    }

    public static <T> T createProxy(Class<T> interfaceClass, ZkServiceDiscovery zkServiceDiscovery, String serviceName, RpcRequestTransport rpcRequestTransport) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new RpcProxy(serviceName, rpcRequestTransport));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 获取方法名
        String methodName = method.getName();
        // 处理特殊方法,避免发送到远程服务
        Object proxy1 = methodInterceptd(proxy, args, methodName);
        if (proxy1 != null) return proxy1;

        log.info("method: {}", methodName);

        Class<?> returnType = method.getReturnType();
        // 获取参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 获取参数列表

        // 封装请求
        RpcRequest rpcRequest = RpcRequest.builder().serviceName(serviceName).methodName(methodName).parameters(args).paramTypes(parameterTypes).returnType(returnType).build();
        RpcResponse rpcResponse = rpcRequestTransport.sendRpcRequest(rpcRequest);
        return rpcResponse.getData();
    }

    private Object methodInterceptd(Object proxy, Object[] args, String methodName) {
        // 特殊方法处理: toString
        if (methodName.equals("toString")) {
            log.info("拦截到 toString");
            return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy)) + ", with InvocationHandler " + this.toString();
        }
        // 处理特殊方法：hashCode
        if (methodName.equals("hashCode")) {
            return System.identityHashCode(proxy);
        }

        // 处理特殊方法：equals
        if (methodName.equals("equals")) {
            return proxy == args[0];
        }
        return null;
    }
}