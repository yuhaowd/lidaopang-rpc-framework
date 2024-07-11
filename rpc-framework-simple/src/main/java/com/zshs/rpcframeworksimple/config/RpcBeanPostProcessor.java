package com.zshs.rpcframeworksimple.config;


import com.zshs.rpcframeworkcommon.utils.NetworkUtil;
import com.zshs.rpcframeworksimple.annotation.RpcReference;
import com.zshs.rpcframeworksimple.annotation.RpcService;
import com.zshs.rpcframeworksimple.proxy.RpcProxy;
import com.zshs.rpcframeworksimple.registry.zk.impl.ZkServiceDiscovery;
import com.zshs.rpcframeworksimple.registry.zk.impl.ZkServiceRegistry;
import com.zshs.rpcframeworksimple.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RpcBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private ZkServiceRegistry zkServiceRegistry;

    @Resource
    private ZkServiceDiscovery zkServiceDiscovery;

    @Resource
    private RpcRequestTransport rpcRequestTransport;


    @Value("${spring.application.name}")
    private String name;

    // socketServer端口
    @Value("${rpc.server.netty.port}")
    private int nettyPort;

    @Value("${rpc.server.socket.port}")
    private int socketPort;

    private static String getString(Class<?> beanClass) {
        Class<?>[] interfaces = beanClass.getInterfaces();
        // 默认该实现类只实现了一个接口
        Class<?> aInterface = interfaces[0];
        String interfaceName = aInterface.getName();
        // 获取实现类的全限定类名
        String implementationName = beanClass.getName();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        String group = rpcService.group();
        String version = rpcService.version();
        // 节点的路径
        return version + "/" + group + "/" + interfaceName + "/" + implementationName;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // 处理RpcService注解
        Class<?> beanClass = bean.getClass();
        // 检查类上是否存在 @RpcService 注解
        if (beanClass.isAnnotationPresent(RpcService.class)) {
            log.info("beanName: {}", beanName);
            String serviceName = getString(beanClass);
            // 获取服务的ip地址
            String ipAddress = NetworkUtil.getLocalIpAddress();
            InetSocketAddress nettyAddress = new InetSocketAddress("127.0.0.1", nettyPort);
            InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", socketPort);
            Map<String, InetSocketAddress> addressMap = new HashMap<>();
            addressMap.put("netty", nettyAddress);
            addressMap.put("socket", socketAddress);
            // 将服务注册到 ServiceRegistryz
            zkServiceRegistry.registerService(serviceName, addressMap);
            log.info("Registered service: {}", serviceName);
        }
        // 处理RpcReference注解
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                // 接口名称
                Class<?> serviceClass = field.getType();
                String interfaceName = serviceClass.getName();
//                Class<?> serviceClass = rpcReference.interfaceClass();
                // 实现类名称
                String implementationName = rpcReference.implementation();
                // 如果指定了实现类，就按照指定的实现类查找。如果没有指定，就按照接口路径往下寻找，如果只有一个则返回，如果有多个则抛出异常
                String group = rpcReference.group();
                String version = rpcReference.version();
                String serviceName = !implementationName.isEmpty() ? version + "/" + group + "/" + interfaceName + "/" + implementationName : version + "/" + group + "/" + interfaceName;
                Object proxy;
                proxy = RpcProxy.createProxy(serviceClass, zkServiceDiscovery, serviceName, rpcRequestTransport);
//                try {
//
//
//                } catch (ClassNotFoundException e) {
//                    throw new BeansException("Implementation class not found: " + implementationName, e) {
//                    };
//                }
//                if (!implementationName.isEmpty()) {
//
//                } else {
//                    proxy = RpcProxy.createProxy(serviceClass, null, null, null, null);
//                }
                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new BeansException("Failed to inject RPC proxy", e) {
                    };
                }
            }
        }
        return bean;
    }
}