package com.zshs.exampleclient.controller;

import com.zshs.exampleserver.service.HelloService;
import com.zshs.exampleserver.service.impl.HelloServiceImpl;
import com.zshs.rpcframeworksimple.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @RpcReference(interfaceClass = HelloService.class, interfaceName = "com.zshs.exampleserver.service.impl.HelloServiceImpl", group = "lidaopang", version = "1.0.0")
    private HelloService helloService;

    @RpcReference(interfaceClass = HelloService.class, interfaceName = "com.zshs.exampleserver.service.impl.HiServiceImpl", group = "lidaopang", version = "1.0.0")
    private HelloService hiService;

    @GetMapping("/hello")
    public String sayHello(String name) {
        String s = helloService.sayHello(name);
        log.info("receive from server result: {}", s);

        return s;
    }
    @GetMapping("/hi")
    public String sayHi(String name) {

        String s = hiService.sayHello(name);
        log.info("receive from server result: {}", s);

        return s;
    }

    @GetMapping("/hello2")
    public String sayHello2(String name, Integer age) {

        String s = helloService.sayHello(name, age);
        log.info("receive from server result: {}", s);

        return s;
    }
}