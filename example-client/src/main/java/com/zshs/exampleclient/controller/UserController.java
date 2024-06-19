package com.zshs.exampleclient.controller;

import com.zshs.exampleserver.service.HelloService;
import com.zshs.rpcframeworksimple.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @RpcReference(HelloService.class)
    private HelloService helloService;

    @GetMapping("/hello")
    public String sayHello(String name) {

        String s = helloService.sayHello(name);
        log.info("receive from server result: {}", s);

        return s;
    }


}
