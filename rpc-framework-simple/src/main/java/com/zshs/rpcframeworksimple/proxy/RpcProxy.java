package com.zshs.rpcframeworksimple.proxy;

import com.zshs.rpcframeworksimple.registry.zk.impl.ZkServiceDiscovery;
import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.socket.RpcClient;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

@Slf4j
public class RpcProxy implements InvocationHandler {


    private final String interfaceName;
    private final ZkServiceDiscovery zkServiceDiscovery;
    private final String serviceName;


    public RpcProxy(String interfaceName, ZkServiceDiscovery zkServiceDiscovery, String serviceName) {
        this.interfaceName = interfaceName;
        this.zkServiceDiscovery = zkServiceDiscovery;
        this.serviceName = serviceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        InetSocketAddress inetSocketAddress = zkServiceDiscovery.lookupService(serviceName);
        if (inetSocketAddress == null) {
            log.info("获取服务失败");
            return null;
        }
        // 获取方法名
        String methodName = method.getName();
        log.info("methodName:{}", methodName);
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
        log.info("addr: {}", inetSocketAddress.getAddress().getHostAddress());
        RpcResponse<String> result = (RpcResponse<String>) rpcClient.send(rpcRequest, inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
        return result.getData();
    }

    public static <T> T createProxy(Class<T> interfaceClass, Class<?> implClass, ZkServiceDiscovery zkServiceDiscovery, String serviceName) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy(implClass.getName(), zkServiceDiscovery, serviceName)
        );
    }
}
