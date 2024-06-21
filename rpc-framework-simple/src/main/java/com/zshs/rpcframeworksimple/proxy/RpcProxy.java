package com.zshs.rpcframeworksimple.proxy;

import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import com.zshs.rpcframeworksimple.remoting.transport.socket.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;

@Slf4j
public class RpcProxy implements InvocationHandler {

    private final String interfaceName;

    private final InetSocketAddress inetSocketAddress;

    public RpcProxy(String interfaceName, InetSocketAddress inetSocketAddress) {
        this.interfaceName = interfaceName;
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取接口的全限定名
        String interfaceName = method.getDeclaringClass().getName();
        log.info("interfaceName:{}", interfaceName);
        // 获取方法名
        String methodName = method.getName();
        log.info("methodName:{}", methodName);
        // 获取参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 获取参数列表
        Object[] parameters = args;
        // 封装请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(this.interfaceName)
                .methodName(methodName)
                .parameters(parameters)
                .paramTypes(parameterTypes)
                .build();

        RpcClient rpcClient = new RpcClient();
        log.info("addr: {}", this.inetSocketAddress.getAddress().getHostAddress());
        RpcResponse<String> result = (RpcResponse<String>) rpcClient.send(rpcRequest, this.inetSocketAddress.getAddress().getHostAddress(), this.inetSocketAddress.getPort());

        return result.getData();
    }

    public static <T> T createProxy(Class<T> interfaceClass, Class<?> implClass, InetSocketAddress inetSocketAddress) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy(implClass.getName(), inetSocketAddress)
        );
    }
}
