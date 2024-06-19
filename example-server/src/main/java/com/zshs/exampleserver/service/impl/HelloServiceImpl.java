package com.zshs.exampleserver.service.impl;

import com.zshs.exampleserver.service.HelloService;
import com.zshs.rpcframeworksimple.annotation.RpcService;
import org.springframework.stereotype.Component;


@Component
@RpcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "Hello:" + name;
    }

    @Override
    public String sayHello(String name, int age) {
        return "hello:" + name + ":" + age;
    }
}