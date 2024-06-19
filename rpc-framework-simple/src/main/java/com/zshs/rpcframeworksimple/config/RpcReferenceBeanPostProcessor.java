package com.zshs.rpcframeworksimple.config;

import com.zshs.rpcframeworksimple.annotation.RpcReference;
import com.zshs.rpcframeworksimple.proxy.RpcProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;

@Component
public class RpcReferenceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference rpcService = field.getAnnotation(RpcReference.class);
                Object proxy = RpcProxy.createProxy(rpcService.value());
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