package com.zshs.rpcframeworksimple.config;

import com.zshs.rpcframeworkcommon.registry.ServiceRegistry;
import com.zshs.rpcframeworksimple.annotation.RpcReference;
import com.zshs.rpcframeworksimple.annotation.RpcService;
import com.zshs.rpcframeworksimple.proxy.RpcProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;

@Component
@Slf4j
public class RpcReferenceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 处理RpcService注解
        Class<?> beanClass = bean.getClass();

        // 检查类上是否存在 @RpcService 注解
        if (beanClass.isAnnotationPresent(RpcService.class)) {
            log.info("beanName:{}", beanName);
            // 使用类的全限定名作为服务名称
            String serviceName = beanClass.getName();
            // 将服务注册到 ServiceRegistry
            ServiceRegistry.register(serviceName, bean);
            log.info("Registered service: {}", serviceName);
        }

        // 处理RpcReference注解
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                Class<?> serviceClass = rpcReference.value();
                String implementation = rpcReference.implementation();
                Object proxy;

                if (!implementation.isEmpty()) {
                    try {
                        Class<?> implClass = Class.forName(implementation);
                        log.info("Creating proxy for service: {}", implClass.getName());
                        proxy = RpcProxy.createProxy(serviceClass, implClass);
                    } catch (ClassNotFoundException e) {
                        throw new BeansException("Implementation class not found: " + implementation, e) {};
                    }
                } else {
                    proxy = RpcProxy.createProxy(serviceClass, null);
                }

                field.setAccessible(true);
                try {
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new BeansException("Failed to inject RPC proxy", e) {};
                }
            }
        }
        return bean;
    }
}