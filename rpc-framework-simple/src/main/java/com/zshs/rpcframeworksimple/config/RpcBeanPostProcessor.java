package com.zshs.rpcframeworksimple.config;


import com.zshs.rpcframeworkcommon.utils.NetworkUtil;
import com.zshs.rpcframeworksimple.annotation.RpcReference;
import com.zshs.rpcframeworksimple.annotation.RpcService;
import com.zshs.rpcframeworksimple.proxy.RpcProxy;
import com.zshs.rpcframeworksimple.registry.zk.impl.ZkServiceDiscovery;
import com.zshs.rpcframeworksimple.registry.zk.impl.ZkServiceRegistry;
import com.zshs.rpcframeworksimple.remoting.transport.netty.client.RpcNettyClient;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.common.util.report.qual.ReportOverride;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;

@Component
@Slf4j
public class RpcBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private ZkServiceRegistry zkServiceRegistry;

    @Resource
    private ZkServiceDiscovery zkServiceDiscovery;

    @Resource
    private RpcNettyClient rpcNettyClient;


    @Value("${spring.application.name}")
    private String name;

    // socketServer端口
    @Value("${rpc.server.netty.port}")
    private int port;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // 处理RpcService注解
        Class<?> beanClass = bean.getClass();

        // 检查类上是否存在 @RpcService 注解
        if (beanClass.isAnnotationPresent(RpcService.class)) {
            log.info("beanName: " + beanName);
            // 使用类的全限定名作为服务名称
            String serviceName = beanClass.getName();
            RpcService rpcService = beanClass.getAnnotation(RpcService.class);
            String group = rpcService.group();
            String version = rpcService.version();
            // 节点的路径
            serviceName = group + "/" + version + "/" + serviceName;
            // 获取服务的ip地址
            String ipAddress = NetworkUtil.getLocalIpAddress();
            InetSocketAddress address = new InetSocketAddress("127.0.0.1", port); // 你需要根据实际情况获取地址
            // 将服务注册到 ServiceRegistry
            zkServiceRegistry.registerService(serviceName, address);
            log.info("Registered service: " + serviceName);
        }
        // 处理RpcReference注解
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                Class<?> serviceClass = rpcReference.interfaceClass();
                String interfaceName = rpcReference.interfaceName();
                String group = rpcReference.group();
                String version = rpcReference.version();
                String serviceName = group + "/" + version + "/" + interfaceName;
                Object proxy;
                if (!interfaceName.isEmpty()) {
                    try {
                        Class<?> implClass = Class.forName(interfaceName);
                        log.info("Creating proxy for service: " + implClass.getName());

                        proxy = RpcProxy.createProxy(serviceClass, implClass, zkServiceDiscovery, serviceName, rpcNettyClient);
                    } catch (ClassNotFoundException e) {
                        throw new BeansException("Implementation class not found: " + interfaceName, e) {
                        };
                    }
                } else {
                    proxy = RpcProxy.createProxy(serviceClass, null, null, null, null);
                }
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