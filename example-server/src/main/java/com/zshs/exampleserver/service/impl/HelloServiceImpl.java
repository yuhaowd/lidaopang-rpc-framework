package com.zshs.exampleserver.service.impl;

import com.zshs.exampleserver.service.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "Hello:" +name;
    }
}