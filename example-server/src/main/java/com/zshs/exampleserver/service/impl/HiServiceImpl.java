package com.zshs.exampleserver.service.impl;


import com.zshs.exampleserver.service.HelloService;
import com.zshs.rpcframeworksimple.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
//@RpcService
public class HiServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hi:" + name;
    }

    @Override
    public String sayHello(String name, int age) {
        return "hi:" + name + ":" + age;
    }
}